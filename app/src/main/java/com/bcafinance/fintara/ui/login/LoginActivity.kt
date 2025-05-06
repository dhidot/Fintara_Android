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
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.viewModel.LoginViewModel
import com.bcafinance.fintara.databinding.ActivityLoginBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity
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

        viewModel.successMessage.observe(this) {
            showSnackbar(it, true)
            viewModel.token.observe(this) { token ->
                // Simpan token di SharedPreferences atau lainnya
                Log.d("LoginActivity", "Token: $token")
            }
        }

        viewModel.errorMessage.observe(this, Observer { message ->
            showSnackbar(message, false)  // Menampilkan pesan error yang diproses
        })

        viewModel.token.observe(this) { token ->
            // Simpan token
            sessionManager.saveToken(token)

            // Sementara hardcode nama user
            sessionManager.saveUserName("Nama Pengguna") // Ganti jika backend mengirim nama

            // Arahkan ke DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}
