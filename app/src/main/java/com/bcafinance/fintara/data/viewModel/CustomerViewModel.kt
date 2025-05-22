package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.customer.UserWithCustomerResponse
import com.bcafinance.fintara.data.repository.CustomerRepository
import kotlinx.coroutines.launch
import android.util.Log
import okhttp3.MultipartBody

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val _profile = MutableLiveData<UserWithCustomerResponse?>()
    val profile: LiveData<UserWithCustomerResponse?> get() = _profile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    fun fetchProfile(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getProfile(userId)
                Log.d("CustomerViewModel", "userId dari SessionManager: $userId")
                _profile.postValue(response.data)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun uploadKtp(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            val result = repository.uploadKtp(filePart)
            _uploadResult.postValue(result)
        }
    }

    fun uploadSelfieKtp(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            val result = repository.uploadSelfieKtp(filePart)
            _uploadResult.postValue(result)
        }
    }
}