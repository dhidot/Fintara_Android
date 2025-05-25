package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.customer.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FirstTimeUpdateViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _fieldErrors = MutableLiveData<Map<String, String>?>()
    val fieldErrors: LiveData<Map<String, String>?> = _fieldErrors

    fun updateProfile(request: FirstTimeUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateFirstTimeProfile(request)
                if (response.status == 200) {
                    _successMessage.value = response.message?.toString()
                    _fieldErrors.value = null // Reset field errors kalau berhasil
                } else {
                    _errorMessage.value = response.getFormattedMessages()
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorBodyString = e.response()?.errorBody()?.string()
                    if (!errorBodyString.isNullOrEmpty()) {
                        try {
                            val gson = Gson()
                            val type = com.google.gson.reflect.TypeToken.getParameterized(
                                ApiResponse::class.java,
                                Map::class.java
                            ).type
                            val apiError = gson.fromJson<ApiResponse<Map<String, String>>>(
                                errorBodyString,
                                type
                            )

                            val fieldErrors = apiError.data
                            if (!fieldErrors.isNullOrEmpty()) {
                                _fieldErrors.value = fieldErrors
                                val combinedErrors =
                                    fieldErrors.entries.joinToString("\n") { "${it.value}" }
                                _errorMessage.value = combinedErrors
                            } else {
                                _errorMessage.value = apiError.getFormattedMessages()
                            }
                        } catch (jsonEx: Exception) {
                            _errorMessage.value = "Error parsing: ${jsonEx.message}"
                        }
                    } else {
                        _errorMessage.value = e.message ?: "Terjadi kesalahan jaringan"
                    }
                } else {
                    _errorMessage.value = e.message ?: "Terjadi kesalahan"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
