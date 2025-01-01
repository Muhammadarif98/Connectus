package com.example.connectus.mvvm

import Utils.Companion.supabase
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registrationStatus = MutableLiveData<String>()
    val registrationStatus: MutableLiveData<String> = _registrationStatus

    val nameError = MutableLiveData<String?>()
    val lastNameError = MutableLiveData<String?>()
    val phoneError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val confirmPasswordError = MutableLiveData<String?>()


    suspend fun signUpUser(
        name: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
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

        viewModelScope.launch {
            try {
                // Регистрация пользователя
                val user = supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (e: Exception) {
                Log.e("RegisterFragment", "Ошибка регистрации: ${e.message}")
                _registrationStatus.value = "Ошибка регистрации: ${e.message}"
            }
        }.join() // Ожидание завершения первого потока

        viewModelScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                supabase.auth.sessionStatus.collect { status ->
                    when (status) {
                        is SessionStatus.Authenticated -> {
                            val userId = user?.id
                            val userData = mapOf(
                                "user_id" to userId,
                                "name" to name,
                                "lastName" to lastName,
                                "phone" to phone,
                                "email" to email,
                                "password" to password,
                                "confirmPassword" to confirmPassword,
                                "imageUrl" to "https://imgcdn.stablediffusionweb.com/2024/9/8/9bc3b58a-aca9-4f88-9ecc-6ea2217f7790.jpg",
                                "status" to "default"
                            )

                            // Вставка данных пользователя в таблицу
                            val response = supabase.from("users").insert(userData)

                            // Проверка на ошибки при вставке
                            Log.d(
                                "RegisterFragment",
                                "Данные пользователя успешно сохранены: $userData"
                            )
                            _registrationStatus.value = "Регистрация прошла успешно"
                        }
                        else -> {
                            // Обработка других статусов, если необходимо
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e("RegisterFragment", "Ошибка регистрации: ${e.message}")
                _registrationStatus.value = "Ошибка регистрации: ${e.message}"
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
