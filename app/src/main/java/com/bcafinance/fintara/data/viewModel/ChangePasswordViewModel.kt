package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.customer.ChangePasswordRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _changePasswordResult = MutableLiveData<Result<String>>()
    val changePasswordResult: LiveData<Result<String>> = _changePasswordResult

    fun changePassword(request: ChangePasswordRequest) {
        viewModelScope.launch {
            try {
                val response = authRepository.changePassword(request)
                if (response.status == 200) {
                    _changePasswordResult.postValue(Result.success(response.getFormattedMessages()))
                } else {
                    _changePasswordResult.postValue(Result.failure(Exception(response.getFormattedMessages())))
                }
            } catch (e: Exception) {
                _changePasswordResult.postValue(Result.failure(e))
            }
        }
    }
}
