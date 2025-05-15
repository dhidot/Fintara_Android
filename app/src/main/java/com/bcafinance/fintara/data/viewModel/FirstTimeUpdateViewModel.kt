package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.auth.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class FirstTimeUpdateViewModel(private val repository: CustomerRepository) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun updateProfile(request: FirstTimeUpdateRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.updateFirstTimeProfile(request)
                _successMessage.value = response.message?.toString()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
