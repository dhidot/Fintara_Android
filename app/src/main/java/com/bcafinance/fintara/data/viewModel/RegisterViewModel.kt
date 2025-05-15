package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.bcafinance.fintara.data.model.dto.auth.RegisterRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    fun registerUser(request: RegisterRequest) {
        isLoading.value = true
        viewModelScope.launch {
            repository.register(
                request = request,
                onSuccess = { message ->
                    isLoading.value = false
                    successMessage.value = message
                },
                onError = { error ->
                    isLoading.value = false
                    // Pastikan error message berformat string yang bisa diproses
                    errorMessage.value = error // Error bisa berupa JSON atau string biasa
                }
            )
        }
    }
}
