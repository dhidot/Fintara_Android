package com.bcafinance.fintara.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.RegisterViewModel

class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

    // Menggunakan signature pertama
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            RegisterViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}