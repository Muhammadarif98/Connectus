package com.example.connectus.mvvm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {

    private val _registrationStatus = MutableLiveData<String>()
    val registrationStatus: MutableLiveData<String> = _registrationStatus

    val nameError = MutableLiveData<String?>()
    val lastNameError = MutableLiveData<String?>()
    val phoneError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val confirmPasswordError = MutableLiveData<String?>()
    //private var progressDialogSignUp : ProgressDialog    = ProgressDialog(this)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


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
       // progressDialogSignUp.show()
        //progressDialogSignUp.setMessage("Регистрация пользователя...")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                //progressDialogSignUp.dismiss()
                val userId = auth.currentUser
                val hashMap = hashMapOf(
                    "userId" to userId!!.uid,
                    "userName" to name, // Убедитесь, что имя пользователя записано
                    "userLastName" to lastName,
                    "userPhone" to phone,
                    "userEmail" to email,
                    "userPassword" to password,
                    "userConfirmPassword" to confirmPassword,
                    "status" to "default",
                    "userImageUrl" to "https://static.vecteezy.com/system/resources/previews/002/002/403/non_2x/man-with-beard-avatar-character-isolated-icon-free-vector.jpg" // Убедитесь, что URL изображения записан
                )

                Log.d("RegisterViewModel", "User data to save: $hashMap") // Логирование данных

                firestore.collection("users").document(userId.uid).set(hashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("RegisterViewModel", "User data saved successfully: $hashMap")
                        } else {
                            Log.e("RegisterViewModel", "Error saving user data", task.exception)
                        }
                    }

                _registrationStatus.value = "Регистрация прошла успешно"
            } else {
                //progressDialogSignUp.dismiss()
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
