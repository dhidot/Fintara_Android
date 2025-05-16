package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.auth.googleLogin.GoogleLoginResponse
import com.bcafinance.fintara.data.model.dto.auth.googleLogin.GoogleLoginResult
import com.bcafinance.fintara.data.model.dto.auth.login.LoginRequest
import com.bcafinance.fintara.data.model.dto.auth.login.LoginResult
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
    val googleLoginResponse = MutableLiveData<GoogleLoginResponse>()


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
                        hasPassword = true
                    )

                },
                onFailure = { error ->
                    Log.e("LoginViewModel", "Login failed: ${error.localizedMessage}")
                    errorMessage.value = parseErrorMessage(error.localizedMessage)
                }
            )
        }
    }


    fun loginWithGoogle(idToken: String, fcmToken: String, deviceInfo: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken, fcmToken, deviceInfo)
            isLoading.value = false

            result.fold(
                onSuccess = { response ->
                    Log.d("LoginViewModel", "Login success: $response")
                    googleLoginResponse.value = response
                },
                onFailure = { error ->
                    Log.e("LoginViewModel", "Login failed: ${error.localizedMessage}")
                    errorMessage.value = parseErrorMessage(error.localizedMessage)
                }
            )
        }
    }

}
