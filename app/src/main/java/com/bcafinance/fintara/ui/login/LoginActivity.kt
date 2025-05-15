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
import com.bcafinance.fintara.data.model.dto.auth.LoginRequest
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
//import com.auth0.android.jwt.JWT
import com.auth0.jwt.JWT

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
            .requestIdToken("124749850521-rjebupase6asr2pngv30p8bt2npevgs5.apps.googleusercontent.com") // Ganti sesuai client_id kamu
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val spannableString = SpannableString("Belum punya akun? Register")
        spannableString.setSpan(
            UnderlineSpan(),
            17,
            26,
            0
        )  // Menambahkan garis bawah pada "Register"
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            17,
            26,
            0
        )  // Menambahkan warna biru pada "Register"
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
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

        viewModel.isLoading.observe(this) {
            binding.progressLogin.visibility = if (it) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !it
        }

        // Di bagian observer successMessage dan loginResult
        viewModel.successMessage.observe(this) { message ->
            showSnackbar(message, true)

            val token = viewModel.token.value
            val isFirstLogin = viewModel.firstLogin.value ?: false
            val userName = viewModel.userName.value ?: "Nama Pengguna"

            if (!token.isNullOrBlank()) {
                val jwt = JWT.decode(token) // Dekode token JWT
                val userId = jwt.getClaim("userId").asString()

                // Simpan ID ke session manager
                sessionManager.saveUserId(userId ?: "Unknown")
                sessionManager.saveToken(token)
                sessionManager.saveUserName(userName)
                sessionManager.saveFirstLogin(isFirstLogin)

                // Ambil akun Google dan simpan profil
                val account = GoogleSignIn.getLastSignedInAccount(this)
                Log.d("GoogleLogin", "Account Email: ${account?.email}")
                Log.d("GoogleLogin", "Account Name: ${account?.displayName}")
                Log.d("GoogleLogin", "Account ID Token: ${account?.idToken}")
                account?.let {
                    sessionManager.saveUserProfile(
                        it.displayName ?: "Unknown",
                        it.email ?: "Unknown",
                        it.photoUrl?.toString()
                    )
                }

                // Redirect ke activity yang sesuai berdasarkan firstLogin
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
            val jwt = JWT() // Dekode token JWT
            val userId = jwt.decodeJwt(result.token).getClaim("userId").asString()
            sessionManager.saveUserId(userId)
            Log.d("UserId", "User ID: $userId")
            sessionManager.saveToken(result.token)
            Log.d("Token", "Token: ${result.token}")

            val intent = if (result.firstLogin) {
                Intent(this, FirstTimeUpdateActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        viewModel.googleLoginResult.observe(this) { result ->
            result.onSuccess { (token, userId, firstLogin) ->
                showSnackbar("Login Google berhasil", true)

                sessionManager.saveUserId(userId)
                sessionManager.saveToken(token)
                Log.d("UserId", "User ID: $userId")
                Log.d("Token", "Token: $token")

                val intent = if (firstLogin) {
                    Intent(this, FirstTimeUpdateActivity::class.java)
                } else {
                    Intent(this, DashboardActivity::class.java)
                }

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            result.onFailure {
                showSnackbar("Login Google gagal: ${it.message}", false)
            }
        }

    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == RC_SIGN_IN) {
                Log.d("GoogleLogin", "onActivityResult called")
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("GoogleLogin", "Google sign-in succeeded: ${account?.displayName}")

                    val idToken = account?.idToken
                    Log.d("GoogleLogin", "ID Token: $idToken")

                    if (!idToken.isNullOrEmpty()) {
                        viewModel.loginWithGoogle(idToken)
                    } else {
                        showSnackbar("ID Token Google kosong", false)
                    }
                } catch (e: ApiException) {
                    Log.e("GoogleLogin", "Google sign in failed", e)
                    showSnackbar("Google login gagal: ${e.message}", false)
                }
            }
        }
}
