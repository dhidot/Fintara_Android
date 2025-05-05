package com.bcafinance.fintara.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.viewModel.LoginViewModel
import com.bcafinance.fintara.databinding.ActivityLoginBinding
import com.bcafinance.fintara.utils.showSnackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                val request = LoginRequest(email, password)
                viewModel.loginUser(request)
            } else {
                showSnackbar("Email dan Password harus diisi", false)
            }
        }

        viewModel.isLoading.observe(this) {
            binding.progressLogin.visibility = if (it) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !it
        }

        viewModel.successMessage.observe(this) {
            showSnackbar(it, true)
            viewModel.token.observe(this) { token ->
                // Simpan token di SharedPreferences atau lainnya
                // Misalnya menggunakan SharedPreferences:
                val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPrefs.edit().putString("token", token).apply()

                // Lakukan navigasi ke halaman berikutnya, misalnya home screen
            }
        }

        viewModel.errorMessage.observe(this) {
            showSnackbar(it, false)
        }
    }
}
