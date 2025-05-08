package com.bcafinance.fintara.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bcafinance.fintara.data.model.dto.LoginRequest
import com.bcafinance.fintara.data.viewModel.LoginViewModel
import com.bcafinance.fintara.databinding.ActivityLoginBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity
import com.bcafinance.fintara.ui.firstTimeUpdate.FirstTimeUpdateActivity
import com.bcafinance.fintara.ui.register.RegisterActivity
import com.bcafinance.fintara.utils.showSnackbar
import com.bcafinance.fintara.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spannableString = SpannableString("Belum punya akun? Register")
        spannableString.setSpan(UnderlineSpan(), 17, 26, 0)  // Menambahkan garis bawah pada "Register"
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 17, 26, 0)  // Menambahkan warna biru pada "Register"
        binding.tvRegisterLink.text = spannableString

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.backgroundTintList = null

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

        viewModel.successMessage.observe(this) { message ->
            showSnackbar(message, true)

            val token = viewModel.token.value
            val isFirstLogin = viewModel.firstLogin.value ?: false
            val userName = viewModel.userName.value ?: "Nama Pengguna"

            Log.d("LoginActivity", "Login success, token: $token, firstLogin: $isFirstLogin")

            if (!token.isNullOrBlank()) {
                sessionManager.saveToken(token)
                sessionManager.saveUserName(userName)

                val intent = if (isFirstLogin) {
                    Intent(this, FirstTimeUpdateActivity::class.java)
                } else {
                    Intent(this, DashboardActivity::class.java)
                }

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                showSnackbar("Token kosong, login gagal", false)
            }
        }


        viewModel.errorMessage.observe(this, Observer { message ->
            showSnackbar(message, false)  // Menampilkan pesan error yang diproses
        })

        viewModel.loginResult.observe(this) { result ->
            showSnackbar(result.message, true)

            sessionManager.saveToken(result.token)

            val intent = if (result.firstLogin) {
                Intent(this, FirstTimeUpdateActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
