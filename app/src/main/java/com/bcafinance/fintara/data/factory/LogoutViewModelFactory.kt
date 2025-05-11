package com.bcafinance.fintara.data.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.viewModel.LogoutViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.bcafinance.fintara.data.repository.AuthRepository

class LogoutViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
            return LogoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
