package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import com.bcafinance.fintara.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val _profile = MutableLiveData<UserWithCustomerResponse?>()
    val profile: LiveData<UserWithCustomerResponse?> get() = _profile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchProfile(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getProfile(token)
                _profile.postValue(response.data)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
