package com.bcafinance.fintara.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.repository.DebtInfoRepository
import com.bcafinance.fintara.data.viewModel.DebtInfoViewModel
import com.bcafinance.fintara.data.viewModel.EditProfileViewModel

class DebtInfoViewModelFactory(private val debtInfoRepository: DebtInfoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebtInfoViewModel::class.java)) {
            return DebtInfoViewModel(debtInfoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}