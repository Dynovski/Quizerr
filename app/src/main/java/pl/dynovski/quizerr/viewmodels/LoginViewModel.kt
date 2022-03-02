package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.data.LoginFormState

class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailEntered(email))
            _loginForm.value = LoginFormState(emailError = R.string.error_no_email)
        else if (!isEmailValid(email))
            _loginForm.value = LoginFormState(emailError = R.string.error_invalid_email)
        else if (!isPasswordEntered(password))
            _loginForm.value = LoginFormState(passwordError = R.string.error_no_password)
        else if (!isPasswordValid(password))
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        else
            _loginForm.value = LoginFormState(isDataValid = true)
    }

    private fun isEmailEntered(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordEntered(password: String): Boolean {
        return password.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}