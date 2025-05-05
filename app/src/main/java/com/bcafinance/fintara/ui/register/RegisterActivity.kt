package com.bcafinance.fintara.ui.register

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.RegisterRequest
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spinnerJenisKelamin: Spinner
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI components
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        spinnerJenisKelamin = findViewById(R.id.spinnerJenisKelamin)
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.backgroundTintList = null
        // Set adapter for spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.jenis_kelamin, // Tambahkan array jenis kelamin di `strings.xml`
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenisKelamin.adapter = spinnerAdapter

        // Set onClickListener for register button
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val jenisKelamin = spinnerJenisKelamin.selectedItem.toString()
            val password = etPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                val registerRequest = RegisterRequest(name, email, jenisKelamin, password)
                val gson = Gson()
                Log.d("RegisterActivity", "RegisterRequest JSON: ${gson.toJson(registerRequest)}")
                registerUser(registerRequest)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("RegisterActivity", "Register started")

                val response = RetrofitClient.apiService.registerCustomer(registerRequest)
                Log.d("RegisterActivity", "Response code: ${response.code()}") // Untuk memeriksa status code
                Log.d("RegisterActivity", "Response message: ${response.message()}") // Untuk memeriksa status message

                // Periksa apakah body respons tidak null
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body() // Ini sekarang sudah terisi dengan data

                    // Log untuk memverifikasi data yang diterima
                    Log.d("RegisterActivity", "Response body: ${registerResponse?.data}")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        // Handle success, misalnya menampilkan data customer
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Log.e("RegisterActivity", "HTTP error: ${e.message()}")
                    Toast.makeText(this@RegisterActivity, "Failed to register: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Log.e("RegisterActivity", "Network error: ${e.message}")
                    Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("RegisterActivity", "Unexpected error: ${e.message}")
                    Toast.makeText(this@RegisterActivity, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
