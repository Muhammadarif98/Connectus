package com.example.connectus.ui.Fragments.signin

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var resendTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        val view = binding.root

        binding.editTextLoginResetPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.editTextLoginResetPassword.text.toString()
                if (!isValidEmail(email)) {
                    binding.editTextLoginResetPassword.error = "Неверный формат email адреса"
                } else {
                    binding.editTextLoginResetPassword.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })

        binding.btnResetPassword.setOnClickListener {
            val email = binding.editTextLoginResetPassword.text.toString()
            if (email.isEmpty()) {
                binding.editTextLoginResetPassword.error = "Введите почту"
                Toast.makeText(
                    requireContext(),
                    "Заполните поле электронной почты",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            startResendTimer()
            resetPassword(email)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun showCustomToast() {
        val layoutInflater = layoutInflater
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }


    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showCustomToast()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Ошибка при отправке email для сброса пароля",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun startResendTimer() {
        binding.btnResetPassword.isEnabled = false
        resendTimer = object : CountDownTimer(60000, 1000) { // 1 минута (60000 мс)
            override fun onTick(millisUntilFinished: Long) {
                binding.resendText.text = "Повторить через ${millisUntilFinished / 1000} секунд"
            }

            override fun onFinish() {
                binding.btnResetPassword.isEnabled = true
                binding.resendText.text = "Отправить повторно"
            }
        }.start()
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel()
    }
}
