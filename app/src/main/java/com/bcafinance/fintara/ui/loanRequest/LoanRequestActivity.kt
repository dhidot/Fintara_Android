package com.bcafinance.fintara.ui.loanRequest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.factory.LoanViewModelFactory
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.repository.LoanRepository
import com.bcafinance.fintara.data.viewModel.LoanRequestState
import com.bcafinance.fintara.data.viewModel.LoanViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal

class LoanRequestActivity : AppCompatActivity() {

    private lateinit var viewModel: LoanViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etAmount: EditText
    private lateinit var etTenor: EditText
    private lateinit var btnAjukan: Button
    private lateinit var progressBar: ProgressBar

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) getLocation()
        else Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_request)

        etAmount = findViewById(R.id.etAmount)
        etTenor = findViewById(R.id.etTenor)
        btnAjukan = findViewById(R.id.btnAjukan)
        progressBar = findViewById(R.id.progressBar)

        val repository = LoanRepository(RetrofitClient.loanApiService)
        viewModel = ViewModelProvider(this, LoanViewModelFactory(repository))[LoanViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnAjukan.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                getLocation()
            }
        }

        observeState()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    submitLoanRequest()
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Permission error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitLoanRequest() {
        val amountStr = etAmount.text.toString()
        val tenorStr = etTenor.text.toString()

        if (amountStr.isBlank() || tenorStr.isBlank()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        val request = LoanRequest().apply {
            amount = BigDecimal(amountStr)
            tenor = tenorStr.toIntOrNull()
            latitude = this@LoanRequestActivity.latitude
            longitude = this@LoanRequestActivity.longitude
        }

        viewModel.createLoanRequest(request)
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is LoanRequestState.Loading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                        btnAjukan.isEnabled = false
                    }
                    is LoanRequestState.Success -> {
                        progressBar.visibility = ProgressBar.GONE
                        btnAjukan.isEnabled = true
                        Toast.makeText(this@LoanRequestActivity, "Pengajuan berhasil", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    is LoanRequestState.Error -> {
                        progressBar.visibility = ProgressBar.GONE
                        btnAjukan.isEnabled = true
                        Toast.makeText(this@LoanRequestActivity, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    LoanRequestState.Idle -> Unit
                }
            }
        }
    }
}
