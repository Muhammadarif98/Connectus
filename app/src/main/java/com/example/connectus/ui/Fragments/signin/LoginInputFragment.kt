package com.example.connectus.ui.Fragments.signin

import Utils.Companion.supabase
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentLoginInputFragmentBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginInputFragment : Fragment() {
    private var _binding: FragmentLoginInputFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialogSignIn: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        progressDialogSignIn = ProgressDialog(requireContext())


        editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = editTextLogin.text.toString()
                if (!isValidEmail(email)) {
                    editTextLogin.error = "Неверный формат email адреса"
                } else {
                    editTextLogin.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })
        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = editTextPassword.text.toString()
                if (password.length < 6) {
                    editTextPassword.error = "Пароль должен содержать не менее 6 символов"
                } else {
                    editTextPassword.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginInputFragment_to_registerFragment)
        }


        btnSign.setOnClickListener {
            login()
        }

        return view
    }

    private fun login() {
        val email = binding.editTextLogin.text.toString()
        val password = binding.editTextPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            binding.editTextLogin.error = "Введите почту"
            binding.editTextPassword.error = "Введите пароль"
            return
        }
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Пожалуйста, подождите...")

        lifecycleScope.launch {
            val user = supabase.auth.currentUserOrNull()?.userMetadata?.get("name").toString()
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                findNavController().navigate(R.id.action_loginInputFragment_to_mainFragment)
                progressDialogSignIn.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Вы вошли в аккаунт",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("LoginFragment", "login $user")
            } catch (e: Exception) {
                progressDialogSignIn.dismiss()
                Toast.makeText(requireContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
                Log.e("LoginFragment", "Error during sign in", e)
            }
        }


    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }

}
