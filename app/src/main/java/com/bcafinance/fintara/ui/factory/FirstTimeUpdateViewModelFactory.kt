package com.bcafinance.fintara.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.FirstTimeUpdateViewModel

class FirstTimeUpdateViewModelFactory(private val customerRepository: CustomerRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FirstTimeUpdateViewModel(customerRepository) as T
    }
}
