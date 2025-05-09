package com.bcafinance.fintara.ui.customer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.databinding.ActivityDetailAkunBinding
import com.bcafinance.fintara.ui.factory.CustomerViewModelFactory
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.utils.showSnackbar

class DetailAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAkunBinding
    private lateinit var sessionManager: SessionManager

    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(CustomerRepository(RetrofitClient.customerApiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val token = sessionManager.getToken()

        if (!token.isNullOrEmpty()) {
            // Tampilkan ProgressBar
            binding.progressBar.visibility = android.view.View.VISIBLE

            customerViewModel.fetchProfile()

            customerViewModel.profile.observe(this) { profile ->
                // Sembunyikan ProgressBar setelah data berhasil di-fetch
                binding.progressBar.visibility = android.view.View.GONE

                binding.tvNama.text = "${profile?.name ?: "-"}"
                binding.tvEmail.text = "${profile?.email ?: "-"}"
                binding.tvJenisKelamin.text = "${profile?.customerDetails?.jenisKelamin ?: "-"}"
                binding.tvTtl.text = "${profile?.customerDetails?.ttl ?: "-"}"
                binding.tvAlamat.text = "${profile?.customerDetails?.alamat ?: "-"}"
                binding.tvPhone.text = "${profile?.customerDetails?.noTelp ?: "-"}"
                binding.tvNIK.text = "${profile?.customerDetails?.nik ?: "-"}"
                binding.tvNamaIbu.text = "${profile?.customerDetails?.namaIbuKandung ?: "-"}"
                binding.tvPekerjaan.text = "${profile?.customerDetails?.pekerjaan ?: "-"}"
                binding.tvGaji.text = "${profile?.customerDetails?.gaji ?: "-"}"
                binding.tvRekening.text = "${profile?.customerDetails?.noRek ?: "-"}"
                binding.tvStatusRumah.text = "${profile?.customerDetails?.statusRumah ?: "-"}"

                val plafond = profile?.customerDetails?.plafond
                binding.tvPlafondType.text = "${plafond?.name ?: "-"}"
                binding.tvPlafondMax.text = "Rp${plafond?.maxAmount ?: "-"}"
                binding.tvBunga.text = "${plafond?.interestRate ?: "-"}%"
                binding.tvTenor.text = "${plafond?.minTenor} - ${plafond?.maxTenor} bulan"
            }

            customerViewModel.error.observe(this) { error ->
                // Sembunyikan ProgressBar jika error terjadi
                binding.progressBar.visibility = android.view.View.GONE
                showSnackbar(error, isSuccess = false)
            }

        } else {
            showSnackbar("Token tidak ditemukan. Silakan login ulang.", isSuccess = false)
            finish()
        }

        setupButtonActions()
    }

    private fun setupButtonActions() {
        binding.btnLengkapiProfil.setOnClickListener {
            // Aksi untuk melengkapi profil
            showSnackbar("Fitur ini belum tersedia", isSuccess = false)
        }
    }
}
