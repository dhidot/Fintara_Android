package com.bcafinance.fintara.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.repository.PlafondRepository
import com.bcafinance.fintara.data.viewModel.PlafondViewModel

class PlafondViewModelFactory(
    private val repository: PlafondRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlafondViewModel::class.java)) {
            return PlafondViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
