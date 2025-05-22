package com.bcafinance.fintara.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.dto.auth.RegisterRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.RegisterViewModel
import com.bcafinance.fintara.databinding.ActivityRegisterBinding
import com.bcafinance.fintara.data.factory.RegisterViewModelFactory
import com.bcafinance.fintara.ui.common.StatusBottomSheetDialogFragment
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.utils.showSnackbar
import com.bcafinance.fintara.utils.parseFieldValidationErrors
import com.bcafinance.fintara.utils.parseErrorMessage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val authRepository = AuthRepository()

    // Menggunakan ViewModel dengan ViewModelProvider.Factory
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                val registerRequest = RegisterRequest(name, email, password)
                viewModel.registerUser(registerRequest)
            } else {
                showSnackbar("All fields are required", false)
            }
        }

        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        })

        viewModel.successMessage.observe(this, Observer { message ->
            val dialog = StatusBottomSheetDialogFragment.newInstance(
                isSuccess = true,
                message = "$message\nSilakan cek email untuk verifikasi."
            )

            dialog.setOnStatusActionListener(object : StatusBottomSheetDialogFragment.OnStatusActionListener {
                override fun onSuccessAction() {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                override fun onErrorDismiss() {
                    // do nothing
                }
            })

            dialog.show(supportFragmentManager, "SuccessDialog")
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            val dialog = StatusBottomSheetDialogFragment.newInstance(
                isSuccess = false,
                message = parseErrorMessage(message)
            )

            dialog.setOnStatusActionListener(object : StatusBottomSheetDialogFragment.OnStatusActionListener {
                override fun onSuccessAction() {}
                override fun onErrorDismiss() {
                }
            })

            dialog.show(supportFragmentManager, "ErrorDialog")
        })


        val fullText = "Sudah punya akun? Login Sekarang"
        val spannableString = SpannableString(fullText)

        val loginText = "Login Sekarang"
        val startIndex = fullText.indexOf(loginText)
        val endIndex = startIndex + loginText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true // opsional: hilangkan garis bawah
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.primary)
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLoginLink.text = spannableString
        binding.tvLoginLink.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun handleValidationError(errorMessage: String) {
        val fieldErrors = parseFieldValidationErrors(errorMessage)

        if (fieldErrors != null) {
            fieldErrors.forEach { (field, message) ->
                when (field) {
                    "name" -> binding.etName.error = message
                    "email" -> binding.etEmail.error = message
                    "password" -> binding.etPassword.error = message
                    else -> showSnackbar(message, false)
                }
            }
        } else {
            showSnackbar(parseErrorMessage(errorMessage), false)
        }
    }
}
