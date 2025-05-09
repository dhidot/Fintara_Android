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
import com.bcafinance.fintara.config.network.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("322824276751-q7nv7bc0rdorbrh4uuscu88megrk6jv1.apps.googleusercontent.com") // Ganti sesuai client_id kamu
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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

        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RC_SIGN_IN) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (!idToken.isNullOrBlank()) {
                Log.d("GoogleLogin", "ID Token: $idToken")
                viewModel.loginWithGoogle(idToken)

                // Observe login result (pastikan observer hanya dipasang sekali)
                viewModel.loginResult.observe(this) { result ->
                    val intent = if (result.firstLogin) {
                        Intent(this, DashboardActivity::class.java)
                    } else {
                        Intent(this, FirstTimeUpdateActivity::class.java)
                    }

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            } else {
                showSnackbar("Gagal mengambil ID Token dari akun Google", false)
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Sign-In Error: ${e.statusCode} - ${e.localizedMessage}")
            showSnackbar("Google Sign-In gagal: ${e.localizedMessage}", false)
        }
    }

}
