package com.bcafinance.fintara.ui.forgotPassword

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.factory.ForgotPasswordViewModelFactory
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.ForgotPasswordViewModel
import com.bcafinance.fintara.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ForgotPasswordViewModelFactory(AuthRepository())
        )[ForgotPasswordViewModel::class.java]

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                showSnackbar("Email tidak boleh kosong", false)
            } else {
                showLoading(true)
                viewModel.sendResetToken(email)
            }
        }

        viewModel.forgotPasswordResult.observe(this) { result ->
            showLoading(false)
            result.onSuccess {
                showSnackbar(it, true)
                finish()
            }
            result.onFailure {
                showSnackbar(it.message ?: "Gagal mengirim email reset", false)
            }
        }

        val fullText = "Sudah ingat password? Login"
        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                finish()
            }
        }

        val startIndex = fullText.indexOf("Login")
        val endIndex = startIndex + "Login".length
        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvBackToLogin.text = spannable
        binding.tvBackToLogin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvBackToLogin.highlightColor = android.graphics.Color.TRANSPARENT
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnSend.isEnabled = !isLoading
        binding.progressSend.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String, isSuccess: Boolean) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(if (isSuccess) 0xFF4CAF50.toInt() else 0xFFF44336.toInt())
            .show()
    }
}
