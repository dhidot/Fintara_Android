package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.auth.forgotPassword.ForgotPasswordRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _forgotPasswordResult = MutableLiveData<Result<String>>()
    val forgotPasswordResult: LiveData<Result<String>> = _forgotPasswordResult

    fun sendResetToken(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.forgotPassword(ForgotPasswordRequest(email))
                _forgotPasswordResult.postValue(Result.success(response.getFormattedMessages()))
            } catch (e: Exception) {
                _forgotPasswordResult.postValue(Result.failure(e))
            }
        }
    }
}
