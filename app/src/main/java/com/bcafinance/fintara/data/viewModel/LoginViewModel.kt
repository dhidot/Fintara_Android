package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val loginResult = MutableLiveData<LoginResult>()

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

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = authRepository.loginWithGoogle(idToken)
                // Lanjutkan proses seperti login biasa (save token, navigasi, dll)
            } catch (e: Exception) {
                errorMessage.value = "Login Google gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


}
