package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.model.dto.LoginRequest
import com.bcafinance.fintara.data.model.dto.LoginResult
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.utils.parseErrorMessage
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val firstLogin = MutableLiveData<Boolean>()
    val userName = MutableLiveData<String>()

    private val authRepository = AuthRepository()

    // Login Biasa
    val loginResult = MutableLiveData<LoginResult>()

    // Login Google
    val googleLoginResult = MutableLiveData<Result<Triple<String, String, Boolean>>>()


    fun loginUser(request: LoginRequest) {
        isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(request)
            isLoading.value = false

            result.fold(
                onSuccess = { (message, jwtToken, firstLoginStatus) ->
                    Log.d("LoginViewModel", "Login success: $message, token: $jwtToken, firstLogin: $firstLoginStatus")

                    loginResult.value = LoginResult(
                        message = message,
                        token = jwtToken,
                        firstLogin = firstLoginStatus,
                    )

                },
                onFailure = { error ->
                    Log.e("LoginViewModel", "Login failed: ${error.localizedMessage}")
                    errorMessage.value = parseErrorMessage(error.localizedMessage)
                }
            )
        }
    }

    // Fungsi login dengan Google
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = authRepository.loginWithGoogle(idToken)
            isLoading.postValue(false)

            result.fold(
                onSuccess = { (message, jwt, isFirstLogin) ->
                    Log.d("LoginViewModel", "Google login success: $message, token: $jwt, firstLogin: $isFirstLogin")
                    loginResult.postValue(LoginResult(message, jwt, isFirstLogin))
                },
                onFailure = { exception ->
                    Log.e("LoginViewModel", "Google login failed: ${exception.message}")
                    errorMessage.postValue(exception.message ?: "Login Google gagal")
                }
            )
        }
    }

}
