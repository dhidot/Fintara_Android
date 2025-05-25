package com.bcafinance.fintara.ui.customer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.bcafinance.fintara.ui.editProfile.EditProfileActivity
import com.bcafinance.fintara.utils.formatRupiah
// FileUtils
import com.bcafinance.fintara.utils.FileUtils
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation

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
        binding.customToolbar.tvTitle.text = "Detail Akun"

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            customerViewModel.fetchProfile(userId = sessionManager.getUserId() ?: "")
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun observeViewModel() {
        customerViewModel.uploadResult.observe(this) { result ->
            result.onSuccess { url ->
                showSnackbar("Foto profil berhasil di-upload!", isSuccess = true)
                // Update foto profil secara langsung
                Glide.with(this)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.ic_menu_camera)
                    .transform(CropCircleWithBorderTransformation(8, Color.parseColor("#FFD700")))
                    .into(binding.tvPhotoProfile)
            }.onFailure { e ->
                showSnackbar("Upload gagal: ${e.message}", isSuccess = false)
            }
        }

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
                tvPlafondMax.text = formatRupiah(plafond?.maxAmount)
                tvBunga.text = "${plafond?.interestRate ?: "-"}%"
                tvTenor.text = "${plafond?.minTenor ?: "-"} - ${plafond?.maxTenor ?: "-"} bulan"

                Glide.with(this@DetailAkunActivity)
                    .load(profile?.fotoUrl)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.ic_menu_camera)
                    .transform(CropCircleWithBorderTransformation(8, Color.parseColor("#FFD700")))
                    .into(tvPhotoProfile)
            }
            stopAllShimmers()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        customerViewModel.error.observe(this) { error ->
            showSnackbar(error, isSuccess = false)
        }
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
        binding.ivAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnLengkapiProfil.setOnClickListener {
            customerViewModel.profile.value?.let { profile ->
                val intent = Intent(this, EditProfileActivity::class.java).apply {
                    putExtra("EXTRA_NAMA", profile.name)
                    putExtra("EXTRA_EMAIL", profile.email)
                    putExtra("EXTRA_JENIS_KELAMIN", profile.customerDetails?.jenisKelamin)
                    putExtra("EXTRA_TTL", profile.customerDetails?.ttl)
                    putExtra("EXTRA_ALAMAT", profile.customerDetails?.alamat)
                    putExtra("EXTRA_PHONE", profile.customerDetails?.noTelp)
                    putExtra("EXTRA_NIK", profile.customerDetails?.nik)
                    putExtra("EXTRA_NAMA_IBU", profile.customerDetails?.namaIbuKandung)
                    putExtra("EXTRA_PEKERJAAN", profile.customerDetails?.pekerjaan)
                    putExtra("EXTRA_GAJI", profile.customerDetails?.gaji?.toPlainString()) // Kirim String, supaya aman
                    putExtra("EXTRA_NO_REK", profile.customerDetails?.noRek)
                    putExtra("EXTRA_STATUS_RUMAH", profile.customerDetails?.statusRumah)
                }
                startActivity(intent)
            } ?: showSnackbar("Data profil belum tersedia", isSuccess = false)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = FileUtils.uriToFile(this, it)
            val filePart = FileUtils.prepareFilePart("file", file)
            customerViewModel.uploadProfilePhoto(filePart)
        }
    }
}

