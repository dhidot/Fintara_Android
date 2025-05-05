package com.bcafinance.fintara.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.repository.UserRepository

class LoginViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val token = MutableLiveData<String>()

    private val userRepository = UserRepository()

    fun loginUser(request: LoginRequest) {
        isLoading.value = true
        userRepository.login(request, { message, jwtToken ->
            isLoading.value = false
            successMessage.value = message
            token.value = jwtToken // Menyimpan token ke LiveData
        }, { error ->
            isLoading.value = false
            errorMessage.value = error
        })
    }
}
