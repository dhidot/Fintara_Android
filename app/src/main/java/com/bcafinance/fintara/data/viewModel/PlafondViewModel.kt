package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcafinance.fintara.data.model.Plafond
import com.bcafinance.fintara.data.repository.PlafondRepository

class PlafondViewModel(private val plafondRepository: PlafondRepository) : ViewModel() {

    private val _plafonds = MutableLiveData<List<Plafond>>()
    val plafonds: LiveData<List<Plafond>> get() = _plafonds

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchPlafonds() {
        plafondRepository.getPlafonds { plafonds, throwable ->
            if (plafonds != null) {
                Log.d("PlafondViewModel", "Plafond received: $plafonds")
                _plafonds.value = plafonds!!
            } else {
                Log.e("PlafondViewModel", "Error: ${throwable?.message}")
                _error.value = throwable?.message ?: "Unknown error"
            }
        }
    }

}
