package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import com.bcafinance.fintara.data.repository.CustomerRepository
import kotlinx.coroutines.launch
import android.util.Log

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val _profile = MutableLiveData<UserWithCustomerResponse?>()
    val profile: LiveData<UserWithCustomerResponse?> get() = _profile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

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
}