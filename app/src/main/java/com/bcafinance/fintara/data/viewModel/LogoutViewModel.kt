package com.bcafinance.fintara.data.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.config.network.SessionManager
import kotlinx.coroutines.launch

class LogoutViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun logout(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Panggil function logout dari AuthRepository
                val result = authRepository.logout()

                if (result.isSuccess) {
                    onSuccess(result.getOrNull() ?: "Logout berhasil")
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Error logging out")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error occurred during logout")
            }
        }
    }
}
