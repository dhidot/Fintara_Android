package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.data.repository.RepaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepaymentViewModel @Inject constructor(
    private val repository: RepaymentRepository
) : ViewModel() {

    private val _repayments = MutableLiveData<List<RepaymentsScheduleResponse>>()
    val repayments: LiveData<List<RepaymentsScheduleResponse>> = _repayments

    fun fetchRepayments(loanRequestId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getRepayments(loanRequestId)
                Log.d("RepaymentVM", "Fetched repayments list: $result")
                _repayments.value = result
            } catch (e: Exception) {
                Log.e("RepaymentVM", "Error: ${e.message}")
            }
        }
    }
}
