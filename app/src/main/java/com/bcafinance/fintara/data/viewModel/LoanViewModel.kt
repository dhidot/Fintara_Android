package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.model.dto.loan.LoanRequestResponse
import com.bcafinance.fintara.data.repository.LoanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoanRequestState {
    object Loading : LoanRequestState()
    data class Success(val data: LoanRequestResponse) : LoanRequestState()
    data class Error(val message: String) : LoanRequestState()
    object Idle : LoanRequestState()
}

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoanRequestState>(LoanRequestState.Idle)
    val state: StateFlow<LoanRequestState> = _state
    private val _loan = MutableLiveData<LoanRequestResponse?>()
    val loan: LiveData<LoanRequestResponse?> get() = _loan
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun createLoanRequest(request: LoanRequest) {
        viewModelScope.launch {
            try {
                _state.value = LoanRequestState.Loading
                val response = repository.createLoanRequest(request)
                _state.value = LoanRequestState.Success(response.data!!)
            } catch (e: Exception) {
                _state.value = LoanRequestState.Error(e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchInProgressLoan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getInProgressLoan()
                _loan.value = response
            } catch (e: Exception) {
                _loan.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
