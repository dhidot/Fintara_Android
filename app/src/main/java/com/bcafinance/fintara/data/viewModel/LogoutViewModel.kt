package com.bcafinance.fintara.data.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.config.network.SessionManager
import kotlinx.coroutines.launch

class LogoutViewModel(private val context: Context) : ViewModel() {

    private val authRepository = AuthRepository()
    private val sessionManager = SessionManager(context) // Inisialisasi dengan konteks yang benar

    fun logout(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Mendapatkan token dari SessionManager
        val token = sessionManager.getToken()

        if (token != null) {
            viewModelScope.launch {
                try {
                    // Memanggil API untuk logout
                    val result = authRepository.logout()
                    if (result.isSuccess) {
                        onSuccess(result.getOrNull() ?: "Logout berhasil")
                    } else {
                        onError(result.exceptionOrNull()?.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    onError(e.message ?: "Unknown error")
                }
            }
        } else {
            onError("Token tidak ditemukan")
        }
    }
}
