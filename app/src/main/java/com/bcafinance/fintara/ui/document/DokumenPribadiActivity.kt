package com.bcafinance.fintara.ui.document

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.databinding.ActivityDokumenBinding
import com.bcafinance.fintara.ui.uploadKtp.UploadKtpActivity
import com.bcafinance.fintara.ui.uploadSelfieKtp.UploadSelfieKtpActivity

class DokumenPribadiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDokumenBinding
    private lateinit var viewModel: CustomerViewModel
    private val customerProfileDao by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fintara_db"
        ).build().customerProfileDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set MaterialToolbar as ActionBar
        setSupportActionBar(binding.customToolbar.toolbar)
        binding.customToolbar.tvTitle.text = "Dokumen Pribadi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val repository = CustomerRepository(RetrofitClient.customerApiService, customerProfileDao)
        val factory = CustomerViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CustomerViewModel::class.java]

        // Observe profile LiveData
        viewModel.profile.observe(this) { profile ->
            if (profile != null) {
                val ktpUrl = profile.customerDetails?.ktpUrl
                val selfieKtpUrl = profile.customerDetails?.selfieKtpUrl

                val isKtpUploaded = !ktpUrl.isNullOrBlank() && ktpUrl != "-"
                val isSelfieKtpUploaded = !selfieKtpUrl.isNullOrBlank() && selfieKtpUrl != "-"

                // Update KTP
                if (isKtpUploaded) {
                    binding.ktpLayout.setBackgroundResource(R.drawable.bg_card_rounded_green)
                    binding.lottieKtp.setAnimation(R.raw.check_animation)
                    binding.lottieKtp.playAnimation()
                } else {
                    binding.ktpLayout.setBackgroundResource(R.drawable.bg_card_rounded_red)
                    binding.lottieKtp.setAnimation(R.raw.not_check_animation)
                    binding.lottieKtp.playAnimation()
                }

                // Update Selfie KTP
                if (isSelfieKtpUploaded) {
                    binding.selfieKtpLayout.setBackgroundResource(R.drawable.bg_card_rounded_green)
                    binding.lottieSelfieKtp.setAnimation(R.raw.check_animation)
                    binding.lottieSelfieKtp.playAnimation()
                } else {
                    binding.selfieKtpLayout.setBackgroundResource(R.drawable.bg_card_rounded_red)
                    binding.lottieSelfieKtp.setAnimation(R.raw.not_check_animation)
                    binding.lottieSelfieKtp.playAnimation()
                }
            }
        }

        // Panggil fetchProfile
        val userId = SessionManager(this).getUserId() ?: ""
        viewModel.fetchProfile(userId)

        // KTP click -> pindah ke UploadKtpActivity
        binding.ktpLayout.setOnClickListener {
            val intent = Intent(this, UploadKtpActivity::class.java)
            startActivity(intent)
        }

        binding.selfieKtpLayout.setOnClickListener {
            val intent = Intent(this, UploadSelfieKtpActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Kembali ke activity sebelumnya saat tombol back ditekan
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh profile data when activity resumes
        val userId = SessionManager(this).getUserId() ?: ""
        viewModel.fetchProfile(userId)
    }
}