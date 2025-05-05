package com.bcafinance.fintara.ui.register

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.RegisterRequest
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bcafinance.fintara.data.viewModel.RegisterViewModel
import com.bcafinance.fintara.databinding.ActivityRegisterBinding
import com.bcafinance.fintara.utils.showSnackbar

class RegisterActivity : AppCompatActivity() {

    // Binding class untuk activity_register.xml
    private lateinit var binding: ActivityRegisterBinding

    // Initialize ViewModel using the ViewModels property delegate
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Spinner jenis kelamin
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.jenis_kelamin,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJenisKelamin.adapter = spinnerAdapter

        // Klik tombol register
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val jenisKelamin = binding.spinnerJenisKelamin.selectedItem.toString()
            val password = binding.etPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                val registerRequest = RegisterRequest(name, email, jenisKelamin, password)
                viewModel.registerUser(registerRequest) // Memanggil fungsi registerUser di ViewModel
            } else {
                showSnackbar("All fields are required", false)
            }
        }

        // Observers for ViewModel LiveData
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        })

        viewModel.successMessage.observe(this, Observer { message ->
            showSnackbar(message, true)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            handleValidationError(message)
        })
    }

    private fun handleValidationError(errorMessage: String) {
        // Cek apakah pesan mengandung "Validasi gagal: { ... }"
        if (errorMessage.contains("Validasi gagal:")) {
            val fieldErrorMap = mutableMapOf<String, String>()

            val errorPart = errorMessage.substringAfter("Validasi gagal:").trim()
                .removePrefix("{").removeSuffix("}")

            val fieldErrors = errorPart.split(",")

            for (fieldError in fieldErrors) {
                val (field, message) = fieldError.split("=").map { it.trim() }
                fieldErrorMap[field] = message
            }

            // Tampilkan pesan sesuai field
            fieldErrorMap.forEach { (field, message) ->
                when (field) {
                    "name" -> binding.etName.error = message
                    "email" -> binding.etEmail.error = message
                    "password" -> binding.etPassword.error = message
                    "jenisKelamin" -> showSnackbar(message, false) // Spinner tidak punya `setError`, pakai snackbar
                    else -> showSnackbar(message, false)
                }
            }
        } else {
            showSnackbar(errorMessage, false)
        }
    }

}