package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _snapToken = MutableLiveData<String?>()
    val snapToken: LiveData<String?> = _snapToken

    fun fetchSnapToken(repaymentScheduleId: String, amount: Long) {
        viewModelScope.launch {
            try {
                val token = repository.generateSnapToken(repaymentScheduleId, amount)
                if (token != null) {
                    Log.d("PaymentViewModel", "Snap Token from backend: $token")
                    _snapToken.value = token
                } else {
                    Log.e("PaymentViewModel", "Snap Token is null")
                    _snapToken.value = null
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Exception: ${e.message}")
                _snapToken.value = null
            }
        }
    }
}



