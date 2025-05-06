package com.bcafinance.fintara.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.RegisterRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.RegisterViewModel
import com.bcafinance.fintara.databinding.ActivityRegisterBinding
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

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.jenis_kelamin,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJenisKelamin.adapter = spinnerAdapter

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val jenisKelamin = binding.spinnerJenisKelamin.selectedItem.toString()
            val password = binding.etPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                val registerRequest = RegisterRequest(name, email, jenisKelamin, password)
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
            showSnackbar(message, true)

            // Delay sebentar agar user bisa melihat Snackbar terlebih dahulu (opsional)
            binding.root.postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() // Tutup RegisterActivity agar tidak bisa di-back
            }, 1500) // 1.5 detik delay
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            handleValidationError(message)
        })

        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Optional, to prevent back navigation to RegisterActivity
        }
    }

    private fun handleValidationError(errorMessage: String) {
        val fieldErrors = parseFieldValidationErrors(errorMessage)

        if (fieldErrors != null) {
            fieldErrors.forEach { (field, message) ->
                when (field) {
                    "name" -> binding.etName.error = message
                    "email" -> binding.etEmail.error = message
                    "password" -> binding.etPassword.error = message
                    "jenisKelamin" -> showSnackbar(message, false)
                    else -> showSnackbar(message, false)
                }
            }
        } else {
            showSnackbar(parseErrorMessage(errorMessage), false)
        }
    }
}
