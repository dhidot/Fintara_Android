package com.bcafinance.fintara.ui.setPassword

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.factory.SetPasswordViewModelFactory
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.SetPasswordViewModel
import com.bcafinance.fintara.databinding.ActivitySetPasswordBinding
import com.bcafinance.fintara.ui.firstTimeUpdate.FirstTimeUpdateActivity

class SetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetPasswordBinding
    private lateinit var viewModel: SetPasswordViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Inisialisasi sessionManager sebelum digunakan
        sessionManager = SessionManager(this)

        val repository = AuthRepository()
        val factory = SetPasswordViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SetPasswordViewModel::class.java]

        binding.btnSetPassword.setOnClickListener {
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            Log.d("SetPasswordActivity", "Password: $password, Confirm Password: $confirmPassword")
            viewModel.setPassword(password, confirmPassword)
        }

        viewModel.setPasswordResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, it.data ?: "Password berhasil diatur", Toast.LENGTH_LONG).show()

                if (sessionManager.isFirstLogin()) {
                    startActivity(Intent(this, FirstTimeUpdateActivity::class.java))
                } else {
                    finish()
                }
                sessionManager.setShouldSetPassword(false)
            }.onFailure {
                Toast.makeText(this, it.message ?: "Terjadi kesalahan", Toast.LENGTH_LONG).show()
            }
        }
    }
}




