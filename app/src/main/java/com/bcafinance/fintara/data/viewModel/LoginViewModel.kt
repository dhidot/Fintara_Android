package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.utils.parseErrorMessage
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val token = MutableLiveData<String>()

    private val authRepository = AuthRepository()

    fun loginUser(request: LoginRequest) {
        isLoading.value = true
        Log.d("LoginViewModel", "Login request: $request")
        viewModelScope.launch {
            val result = authRepository.login(request)
            isLoading.value = false

            result.fold(
                onSuccess = { (message, jwtToken) ->
                    Log.d("LoginViewModel", "Login success, message: $message, token: $jwtToken")
                    successMessage.value = message
                    token.value = jwtToken // Menyimpan token ke LiveData
                },
                onFailure = { error ->
                    Log.e("LoginViewModel", "Login failed, error: ${error.localizedMessage}")
                    errorMessage.value = parseErrorMessage(error.localizedMessage)  // Gunakan helper function
                }
            )
        }
    }
}
