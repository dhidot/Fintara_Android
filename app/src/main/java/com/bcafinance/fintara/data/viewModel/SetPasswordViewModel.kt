package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.auth.setPassword.SetPasswordRequest
import com.bcafinance.fintara.data.model.dto.auth.setPassword.SetPasswordResponse
import com.bcafinance.fintara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SetPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _setPasswordResult = MutableLiveData<Result<ApiResponse<String>>>()
    val setPasswordResult: LiveData<Result<ApiResponse<String>>> get() = _setPasswordResult

    fun setPassword(password: String, confirmPassword: String) {
        viewModelScope.launch {
            val request = SetPasswordRequest(password, confirmPassword)
            val result = authRepository.setPassword(request)
            _setPasswordResult.postValue(result)
        }
    }
}
