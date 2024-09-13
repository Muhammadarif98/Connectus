package com.example.connectus.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.connectus.data.LoginRepository
import com.example.connectus.data.Result

import com.example.connectus.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
    /*

    // логика авторизации - проверка на валидность логина и пароля
    // результат - LoginResult - содержит информацию о результате авторизации

    // метод login - выполняет авторизацию
    // result - Result.Success - если авторизация прошла успешно
    // result - Result.Error - если авторизация не прошла
    // в случае Result.Success - LoginResult содержит информацию о пользователе
    // в случае Result.Error - LoginResult содержит ошибку

    // метод loginDataChanged - проверяет на валидность логин и пароль
    // LoginResult - содержит информацию о результате проверки
    // LoginResult.usernameError - ошибка валидации логина
    // LoginResult.passwordError - ошибка валидации пароля
    // LoginResult.isDataValid - true - если логин и пароль валидны

    // isUserNameValid - метод-валидатор логина
    // true - если логин валиден
    // false - если логин не валиден

    // isPasswordValid - метод-валидатор пароля
    // true - если пароль валиден
    // false - если пароль не валиден

    */