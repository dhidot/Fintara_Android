package com.bcafinance.fintara.ui.customer

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.databinding.ActivityDetailAkunBinding
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.utils.showSnackbar
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class DetailAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAkunBinding
    private lateinit var sessionManager: SessionManager
    private val customerProfileDao by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fintara_db"
        ).build().customerProfileDao()
    }

    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(CustomerRepository(RetrofitClient.customerApiService, customerProfileDao))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        sessionManager = SessionManager(this)

        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            showSnackbar("Token tidak ditemukan. Silakan login ulang.", isSuccess = false)
            finish()
            return
        }

        startAllShimmers()
        customerViewModel.fetchProfile(userId = sessionManager.getUserId() ?: "")

        observeViewModel()
        setupButtonActions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun observeViewModel() {
        customerViewModel.profile.observe(this) { profile ->
            with(binding) {
                tvNama.text = profile?.name ?: "-"
                tvEmail.text = profile?.email ?: "-"
                tvJenisKelamin.text = profile?.customerDetails?.jenisKelamin ?: "-"
                tvTtl.text = profile?.customerDetails?.ttl ?: "-"
                tvAlamat.text = profile?.customerDetails?.alamat ?: "-"
                tvPhone.text = profile?.customerDetails?.noTelp ?: "-"
                tvNIK.text = profile?.customerDetails?.nik ?: "-"
                tvNamaIbu.text = profile?.customerDetails?.namaIbuKandung ?: "-"
                tvPekerjaan.text = profile?.customerDetails?.pekerjaan ?: "-"
                tvGaji.text = formatRupiah(profile?.customerDetails?.gaji)
                tvRekening.text = profile?.customerDetails?.noRek ?: "-"
                tvStatusRumah.text = profile?.customerDetails?.statusRumah ?: "-"

                val plafond = profile?.customerDetails?.plafond
                tvPlafondType.text = plafond?.name ?: "-"
                tvPlafondMax.text = "Rp${plafond?.maxAmount ?: "-"}"
                tvBunga.text = "${plafond?.interestRate ?: "-"}%"
                tvTenor.text = "${plafond?.minTenor ?: "-"} - ${plafond?.maxTenor ?: "-"} bulan"
            }
            stopAllShimmers()
        }

        customerViewModel.error.observe(this) { error ->
            showSnackbar(error, isSuccess = false)
        }
    }

    private fun formatRupiah(value: BigDecimal?): String {
        return if (value == null) "-" else NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(value)
    }

    private fun startAllShimmers() = with(binding) {
        listOf(
            shimmerNama, shimmerEmail, shimmerJenisKelamin, shimmerTtl, shimmerAlamat,
            shimmerPhone, shimmerNIK, shimmerNamaIbu, shimmerPekerjaan,
            shimmerGaji, shimmerRekening, shimmerStatusRumah, shimmerPlafondType, shimmerPlafondMax, shimmerBunga, shimmerTenor
        ).forEach {
            it.startShimmer()
            it.visibility = View.VISIBLE
        }
    }

    private fun stopAllShimmers() = with(binding) {
        listOf(
            shimmerNama, shimmerEmail, shimmerJenisKelamin, shimmerTtl, shimmerAlamat,
            shimmerPhone, shimmerNIK, shimmerNamaIbu, shimmerPekerjaan,
            shimmerGaji, shimmerRekening, shimmerStatusRumah, shimmerPlafondType, shimmerPlafondMax, shimmerBunga, shimmerTenor
        ).forEach {
            it.stopShimmer()
            it.visibility = View.GONE
        }
    }

    private fun setupButtonActions() {
        binding.btnLengkapiProfil.setOnClickListener {
            showSnackbar("Fitur ini belum tersedia", isSuccess = false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }
}

