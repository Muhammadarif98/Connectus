package com.example.connectus

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.connectus.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        val loginButton = binding.loginButtonTV
        binding.btnSign.setOnClickListener {
            signUpUser()
        }
        binding.termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSign.isEnabled = isChecked
            if (isChecked){

            }else{
                Toast.makeText(requireContext(), "Поставьте галочку", Toast.LENGTH_SHORT).show()
            }

        }
        loginButton.setOnClickListener {
            val loginInputFragment = LoginInputFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, loginInputFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun signUpUser() {
        val name = binding.nameRegisterET.text.toString()
        val lastName = binding.lastNameRegisterET.text.toString()
        val phone = binding.phoneRegisterET.text.toString()
        val email = binding.editTextLogin.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.confirmPasswordET.text.toString()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()|| name.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            binding.nameRegisterET.error = "Заполните все поля"
            binding.lastNameRegisterET.error = "Заполните все поля"
            binding.phoneRegisterET.error = "Заполните все поля"
            binding.editTextLogin.error = "Заполните все поля"
            binding.editTextPassword.error = "Заполните все поля"
            binding.confirmPasswordET.error = "Заполните все поля"
            return
        }
        if (name.length < 3) {
            Toast.makeText(requireContext(), "Имя должно содержать не менее 3 символов", Toast.LENGTH_SHORT).show()
            binding.nameRegisterET.error = "Имя должно содержать не менее 3 символов"
            return
        }
        if (lastName.length < 3) {
            Toast.makeText(requireContext(), "Фамилия должна содержать не менее 3 символов", Toast.LENGTH_SHORT).show()
            binding.lastNameRegisterET.error = "Фамилия должна содержать не менее 3 символов"
            return
        }
        if (phone.length < 10) {
            Toast.makeText(requireContext(), "Номер телефона должен содержать не менее 10 символов", Toast.LENGTH_SHORT).show()
            binding.phoneRegisterET.error = "Номер телефона должен содержать не менее 10 символов"
            return
        }
        if (isValidPhone(phone)){
            Toast.makeText(requireContext(), "Неверный формат номера телефона", Toast.LENGTH_SHORT).show()
            binding.phoneRegisterET.error = "Неверный формат номера телефона"
            return
        }
        if (password.length < 6) {
            Toast.makeText(
                requireContext(),
                "Пароль должен содержать не менее 6 символов",
                Toast.LENGTH_SHORT
            ).show()
            binding.editTextPassword.error = "Пароль должен содержать не менее 6 символов"
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            binding.confirmPasswordET.error = "Пароли не совпадают"
            return
        }
        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Неверный формат email адреса", Toast.LENGTH_SHORT).show()
            binding.editTextLogin.error = "Неверный формат email адреса"
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful) {
                Toast.makeText(requireContext(),
                    "Регистрация прошла успешно",
                    Toast.LENGTH_SHORT).show()
                val loginInputFragment = LoginInputFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, loginInputFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }else{
                if (auth.currentUser != null) {
                    Toast.makeText(requireContext(),
                        "Пользователь с таким email уже существует",
                        Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                Toast.makeText(requireContext(),
                    "Ошибка регистрации",
                    Toast.LENGTH_SHORT).show()
            }
        }
       // viewModel.signUp(email, password)
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^\\+7[0-9]{10}$"
        return phone.matches(phonePattern.toRegex())
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }
}