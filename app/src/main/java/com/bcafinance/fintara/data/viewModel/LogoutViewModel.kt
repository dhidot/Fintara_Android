package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LogoutViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun logout(
        token: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Memanggil API untuk logout
                val result = authRepository.logout(token)
                if (result.isSuccess) {
                    onSuccess(result.getOrNull() ?: "Logout berhasil")
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
