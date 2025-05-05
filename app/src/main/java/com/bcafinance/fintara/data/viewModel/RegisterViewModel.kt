package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.bcafinance.fintara.data.model.RegisterRequest
import com.bcafinance.fintara.utils.parseApiError
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    val isLoading = MutableLiveData(false)
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitClient.apiService.registerCustomer(request)
                if (response.isSuccessful && response.body() != null) {
                    successMessage.value = response.body()?.message ?: "Registrasi berhasil"
                } else {
                    val error = response.errorBody().parseApiError<Any>()
                    errorMessage.value = error?.getFormattedMessages() ?: "Terjadi kesalahan"
                }
            } catch (e: Exception) {
                errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}