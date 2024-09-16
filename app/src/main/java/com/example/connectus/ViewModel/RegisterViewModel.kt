package com.example.connectus.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    private val _registrationStatus = MutableLiveData<String>()
    val registrationStatus: MutableLiveData<String> = _registrationStatus

    val nameError = MutableLiveData<String?>()
    val lastNameError = MutableLiveData<String?>()
    val phoneError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val confirmPasswordError = MutableLiveData<String?>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUpUser(name: String, lastName: String, phone: String, email: String, password: String, confirmPassword: String) {
        nameError.value = null
        lastNameError.value = null
        phoneError.value = null
        emailError.value = null
        passwordError.value = null
        confirmPasswordError.value = null

        when {
            name.isEmpty() -> {
                nameError.value = "Введите имя"
                return
            }
            lastName.isEmpty() -> {
                lastNameError.value = "Введите фамилию"
                return
            }
            phone.isEmpty() -> {
                phoneError.value = "Введите номер телефона"
                return
            }
            email.isEmpty() -> {
                emailError.value = "Введите email"
                return
            }
            password.isEmpty() -> {
                passwordError.value = "Введите пароль"
                return
            }
            confirmPassword.isEmpty() -> {
                confirmPasswordError.value = "Введите подтверждение пароля"
                return
            }
        }

        if (name.length < 3) {
            nameError.value = "Имя должно содержать не менее 3 символов"
            return
        }
        if (lastName.length < 3) {
            lastNameError.value = "Фамилия должна содержать не менее 3 символов"
            return
        }
        if (phone.length < 10) {
            phoneError.value = "Номер телефона должен содержать не менее 10 символов"
            return
        }
        if (password.length < 6) {
            passwordError.value = "Пароль должен содержать не менее 6 символов"
            return
        }
        if (password != confirmPassword) {
            confirmPasswordError.value = "Пароли не совпадают"
            return
        }
        if (!isValidEmail(email)) {
            emailError.value = "Неверный формат email адреса"
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _registrationStatus.value = "Регистрация прошла успешно"
            } else {
                if (auth.currentUser != null) {
                    _registrationStatus.value = "Пользователь с таким email уже существует"
                    return@addOnCompleteListener
                }
                _registrationStatus.value = "Ошибка регистрации"
            }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^\\+7 \\([0-9]{3}\\)-[0-9]{3}-[0-9]{2}-[0-9]{2}$"
        return phone.matches(phonePattern.toRegex())
    }
}
