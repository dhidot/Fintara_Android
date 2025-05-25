package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcafinance.fintara.data.model.dto.customer.DebtInfoResponse
import com.bcafinance.fintara.data.repository.DebtInfoRepository
import kotlinx.coroutines.launch

class DebtInfoViewModel(private val debtInfoRepository: DebtInfoRepository) : ViewModel() {

    private val _debtInfo = MutableLiveData<DebtInfoResponse>()
    val debtInfo: LiveData<DebtInfoResponse> get() = _debtInfo

    fun fetchDebtInfo() {
        viewModelScope.launch {
            val data = debtInfoRepository.getDebtInfo()
            _debtInfo.postValue(data!!)
        }
    }
}