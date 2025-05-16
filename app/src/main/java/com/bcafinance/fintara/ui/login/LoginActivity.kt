package com.bcafinance.fintara.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bcafinance.fintara.data.model.dto.auth.login.LoginRequest
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
import com.google.firebase.messaging.FirebaseMessaging
import com.auth0.jwt.JWT
import com.bcafinance.fintara.ui.setPassword.SetPasswordActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001
    private val viewModel: LoginViewModel by viewModels()

    private var fcmToken: String? = null
    private val deviceInfo: String
        get() = "Model: ${Build.MODEL}, OS: ${Build.VERSION.RELEASE}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                Log.d("FCMToken", "Token: $fcmToken")
            } else {
                Log.w("FCMToken", "Fetching FCM token failed", task.exception)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("124749850521-rjebupase6asr2pngv30p8bt2npevgs5.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val spannableString = SpannableString("Belum punya akun? Register")
        spannableString.setSpan(UnderlineSpan(), 17, 26, 0)
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 17, 26, 0)
        binding.tvRegisterLink.text = spannableString

        binding.tvRegisterLink.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        binding.btnLogin.backgroundTintList = null

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                // Buat request login dengan tambahan fcmToken dan deviceInfo
                val request = LoginRequest(
                    email = email,
                    password = password,
                    fcmToken = fcmToken ?: "",  // pastikan ada token walau kosong
                    deviceInfo = deviceInfo
                )
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

        viewModel.successMessage.observe(this) { message ->
            showSnackbar(message, true)

            val token = viewModel.token.value
            val isFirstLogin = viewModel.firstLogin.value ?: false
            val userName = viewModel.userName.value ?: "Nama Pengguna"

            if (!token.isNullOrBlank()) {
                val jwt = JWT.decode(token)
                val userId = jwt.getClaim("userId").asString()

                sessionManager.saveUserId(userId ?: "Unknown")
                sessionManager.saveToken(token)
                sessionManager.saveUserName(userName)
                sessionManager.saveFirstLogin(isFirstLogin)

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
            showSnackbar(message, false)
        })

        viewModel.loginResult.observe(this) { result ->
            showSnackbar(result.message, true)

            val jwt = JWT.decode(result.token)
            val userId = jwt.getClaim("userId").asString()
            sessionManager.saveUserId(userId)
            sessionManager.saveToken(result.token)
            sessionManager.saveFirstLogin(result.firstLogin) // Tambahkan ini
            Log.d("UserId", "User ID: $userId")

            val intent = if (result.firstLogin) {
                Intent(this, FirstTimeUpdateActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        viewModel.googleLoginResponse.observe(this) { response ->
            showSnackbar(response.message, true)

            sessionManager.saveToken(response.token)
            val jwt = JWT.decode(response.token)
            val userId = jwt.getClaim("userId").asString() ?: "Unknown"
            sessionManager.saveUserId(userId)
            sessionManager.saveFirstLogin(response.firstLogin) // Tambahkan ini

            Log.d("UserId", "User ID: $userId")
            Log.d("Token", "Token: ${response.token}")

            val intent = when {
                !response.hasPassword && response.firstLogin -> {
                    sessionManager.setShouldSetPassword(true) // <-- Tambahkan ini
                    Intent(this, SetPasswordActivity::class.java)
                }
                response.firstLogin -> {
                    sessionManager.setShouldSetPassword(false) // <-- Pastikan flag lain di-reset
                    Intent(this, FirstTimeUpdateActivity::class.java)
                }
                else -> {
                    sessionManager.setShouldSetPassword(false)
                    Intent(this, DashboardActivity::class.java)
                }
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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
                    if (fcmToken.isNullOrEmpty()) {
                        showSnackbar("Mohon tunggu, FCM token belum siap", false)
                        return
                    }

                    viewModel.loginWithGoogle(
                        idToken = idToken,
                        fcmToken = fcmToken ?: "",
                        deviceInfo = deviceInfo
                    )
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
