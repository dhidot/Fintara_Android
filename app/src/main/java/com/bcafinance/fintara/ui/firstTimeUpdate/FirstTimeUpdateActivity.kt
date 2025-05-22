package com.bcafinance.fintara.ui.firstTimeUpdate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.model.dto.customer.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.FirstTimeUpdateViewModel
import com.bcafinance.fintara.databinding.ActivityFirstTimeUpdateBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity
import com.bcafinance.fintara.data.factory.FirstTimeUpdateViewModelFactory
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.utils.showSnackbar
import java.util.Calendar

class FirstTimeUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstTimeUpdateBinding
    private lateinit var viewModel: FirstTimeUpdateViewModel
    private lateinit var sessionManager: SessionManager
    private val customerProfileDao by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fintara_db"
        ).build().customerProfileDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstTimeUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val token = sessionManager.getToken() ?: ""

        val repository = CustomerRepository(RetrofitClient.customerApiService, customerProfileDao)
        val factory = FirstTimeUpdateViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FirstTimeUpdateViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Lengkapi Profil"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.etTtl.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                binding.etTtl.setText(selectedDate)
            }, year, month, day).show()
        }

        val jenisKelaminOptions = listOf("LAKI_LAKI", "PEREMPUAN")
        val rumahOptions = listOf("Rumah Sendiri", "Rumah Orang Tua", "Kontrak")
        val adapterRumah = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rumahOptions)
        val adapterJenisKelamin = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jenisKelaminOptions)
        (binding.etStatusRumah as AutoCompleteTextView).setAdapter(adapterRumah)
        (binding.etJenisKelamin as AutoCompleteTextView).setAdapter(adapterJenisKelamin)

        binding.btnSubmit.setOnClickListener {
            val request = FirstTimeUpdateRequest(
                jenisKelamin = binding.etJenisKelamin.text.toString(),
                ttl = binding.etTtl.text.toString(),
                alamat = binding.etAlamat.text.toString(),
                noTelp = binding.etNoTelp.text.toString(),
                nik = binding.etNik.text.toString(),
                namaIbuKandung = binding.etNamaIbuKandung.text.toString(),
                pekerjaan = binding.etPekerjaan.text.toString(),
                gaji = binding.etGaji.text.toString().toDoubleOrNull() ?: 0.0,
                noRek = binding.etNoRek.text.toString(),
                statusRumah = binding.etStatusRumah.text.toString()
            )
            viewModel.updateProfile(request)
        }

        viewModel.isLoading.observe(this) {
            binding.btnSubmit.isEnabled = !it
        }

        viewModel.successMessage.observe(this) { message ->
            showSnackbar(message, true)

            binding.root.postDelayed({
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }, 2000)

            sessionManager.saveFirstLogin(false)
        }

        viewModel.errorMessage.observe(this) { message ->
            showSnackbar(message, false)
        }
    }
}

