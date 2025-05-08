package com.bcafinance.fintara.data.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest

class FirstLoginViewModel : ViewModel() {

    private val _ttl = MutableLiveData<String>()
    val ttl: LiveData<String> = _ttl

    // Menyimpan data teks
    val textData = MutableLiveData<FirstTimeUpdateRequest>()

    // Menyimpan URI foto KTP dan selfie
    val ktpUri = MutableLiveData<Uri>()
    val selfieUri = MutableLiveData<Uri>()

    // Fungsi untuk mengatur data teks
    fun setTextData(data: FirstTimeUpdateRequest) {
        textData.value = data
    }

    fun setTtl(date: String) {
        _ttl.value = date
    }

    // Fungsi untuk mengatur foto KTP
    fun setKtpUri(uri: Uri) {
        ktpUri.value = uri
    }

    // Fungsi untuk mengatur foto Selfie
    fun setSelfieUri(uri: Uri) {
        selfieUri.value = uri
    }
}
