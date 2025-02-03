package com.example.connectus.ui.Fragments.signin

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.connectus.databinding.FragmentOTPVerificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class OTPVerificationFragment : Fragment() {

    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    // LiveData для ошибок валидации
    private val emailError = MutableLiveData<String>()
    private val passwordError = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            val email = binding.editTextLoginResetPassword.text.toString()
            val password = binding.editTextPassword.text.toString()
            validateInputsAndSendEmail(email, password)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        observeErrorLiveData()
    }

    private fun validateInputsAndSendEmail(email: String, password: String) {
        when {
            email.isEmpty() -> emailError.value = "Введите email"
            !isValidEmail(email) -> emailError.value = "Неверный формат email адреса"
            password.isEmpty() -> passwordError.value = "Введите пароль"
            password.length < 6 -> passwordError.value =
                "Пароль должен содержать не менее 6 символов"

            else -> sendVerificationEmail(email, password)
        }
    }

    private fun sendVerificationEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                showSnackbar("Ссылка для верификации отправлена на почту $email")
                            } else {
                                showSnackbar("Ошибка при отправке email")
                            }
                        }
                } else {
                    showSnackbar("Ошибка при входе в аккаунт")
                }
            }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun observeErrorLiveData() {
        emailError.observe(viewLifecycleOwner, Observer { error ->
            binding.editTextLoginResetPassword.error = error
        })

        passwordError.observe(viewLifecycleOwner, Observer { error ->
            binding.editTextPassword.error = error
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
