package com.bcafinance.fintara.data.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.loan.LoanHistoryResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanInProgressResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanPreviewResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.model.dto.loan.LoanRequestResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanSimulationRequest
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

    private val _previewResult = MutableLiveData<LoanPreviewResponse>()
    val previewResult: LiveData<LoanPreviewResponse> get() = _previewResult

    private val _loan = MutableLiveData<LoanInProgressResponse?>()
    val loan: LiveData<LoanInProgressResponse?> get() = _loan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _loanPreview = MutableLiveData<LoanPreviewResponse>()
    val loanPreview: LiveData<LoanPreviewResponse> = _loanPreview

    private val _approvedLoans = MutableLiveData<List<LoanHistoryResponse>>()
    val approvedLoans: LiveData<List<LoanHistoryResponse>> get() = _approvedLoans

    private val _rejectedLoans = MutableLiveData<List<LoanHistoryResponse>>()
    val rejectedLoans: LiveData<List<LoanHistoryResponse>> get() = _rejectedLoans

    private val _isLoadingApproved = MutableLiveData<Boolean>()
    val isLoadingApproved: LiveData<Boolean> get() = _isLoadingApproved

    private val _isLoadingRejected = MutableLiveData<Boolean>()
    val isLoadingRejected: LiveData<Boolean> get() = _isLoadingRejected

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun simulateLoan(request: LoanSimulationRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.simulateLoan(request)
                _previewResult.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _loading.value = false
            }
        }
    }

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

    fun fetchLoanPreview(request: LoanRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLoanPreview(request)
                _loanPreview.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchApprovedLoans() {
        viewModelScope.launch {
            _isLoadingApproved.value = true      // start loading
            try {
                _approvedLoans.value = repository.getLoanHistory("APPROVED")
            } catch (e: Exception) {
                _approvedLoans.value = emptyList()
            } finally {
                _isLoadingApproved.value = false  // selesai loading
            }
        }
    }


    fun fetchRejectedLoans() {
        viewModelScope.launch {
            _isLoadingRejected.value = true
            try {
                _rejectedLoans.value = repository.getLoanHistory("REJECTED")
            } catch (e: Exception) {
                _rejectedLoans.value = emptyList()
            } finally {
                _isLoadingRejected.value = false
            }
        }
    }
}
