package com.example.connectus.ui.Fragments.signin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.Utils
import com.example.connectus.Utils.Companion.firestore
import com.example.connectus.databinding.FragmentLoginInputFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

class LoginInputFragment : Fragment() {
    private var _binding: FragmentLoginInputFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginInputFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val registerButton = binding.registerButtonTV
        val editTextLogin = binding.editTextLogin
        val editTextPassword = binding.editTextPassword
        val btnSign = binding.btnLogin
        val forgotPassword = binding.forgotPassword
        progressDialogSignIn = ProgressDialog(requireContext())

        editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = editTextLogin.text.toString()
                if (!isValidEmail(email)) {
                    editTextLogin.error = "Неверный формат email адреса"
                } else {
                    editTextLogin.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = editTextPassword.text.toString()
                if (password.length < 6) {
                    editTextPassword.error = "Пароль должен содержать не менее 6 символов"
                } else {
                    editTextPassword.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginInputFragment_to_registerFragment)
        }

        btnSign.setOnClickListener {
            login()
        }

        forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginInputFragment_to_resetPasswordFragment)
        }

        binding.verifity.setOnClickListener {
            findNavController().navigate(R.id.action_loginInputFragment_to_OTPVerificationFragment)
        }

        return view
    }

    private fun login() {
        val email = binding.editTextLogin.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
            binding.editTextLogin.error = "Введите почту"
            binding.editTextPassword.error = "Введите пароль"
            return
        }

        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Пожалуйста, подождите...")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                generateToken()
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    progressDialogSignIn.dismiss()
                    Snackbar.make(binding.root, "Вы вошли в аккаунт", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginInputFragment_to_mainFragment)
                } else {
                    progressDialogSignIn.dismiss()
                    Snackbar.make(binding.root, "Пожалуйста, подтвердите ваш email перед входом", Snackbar.LENGTH_SHORT).show()
                    auth.signOut() // Выходим из аккаунта
                }
            } else {
                progressDialogSignIn.dismiss()
                Snackbar.make(binding.root, "Неверный логин или пароль", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateToken(){
        var token: String = ""
        val firebaseInstance = FirebaseInstallations.getInstance()
        firebaseInstance.id.addOnSuccessListener{installationid->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettocken->
                token = gettocken
                val hashMap = hashMapOf<String, Any>("token" to token)
                firestore.collection("Tokens").document(Utils.getUidLoggedIn()).set(hashMap).addOnSuccessListener {
                }
            }
        }.addOnFailureListener {}
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }
}