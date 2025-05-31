package com.bcafinance.fintara.ui.editProfile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.factory.EditProfileViewModelFactory
import com.bcafinance.fintara.data.model.dto.customer.CustomerUpdateProfileRequestDTO
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.EditProfileViewModel
import com.bcafinance.fintara.databinding.ActivityEditProfileBinding
import com.bcafinance.fintara.utils.formatRupiah
import com.bcafinance.fintara.utils.formatRupiahInput
import com.bcafinance.fintara.utils.showSnackbar

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
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
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = CustomerRepository(RetrofitClient.customerApiService, customerProfileDao)
        val factory = EditProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EditProfileViewModel::class.java]

        viewModel.fieldErrors.observe(this) { errors ->
            // Reset semua error dulu
            binding.etNik.error = null
            binding.etNoTelp.error = null
            binding.etAlamat.error = null
            binding.etTtl.error = null
            binding.etNamaIbu.error = null
            binding.etPekerjaan.error = null
            binding.etGaji.error = null
            binding.etNoRek.error = null
            binding.etStatusRumah.error = null

            errors?.forEach { (field, message) ->
                when (field) {
                    "nik" -> binding.etNik.error = message
                    "noTelp" -> binding.etNoTelp.error = message
                    "alamat" -> binding.etAlamat.error = message
                    "ttl" -> binding.etTtl.error = message
                    "namaIbuKandung" -> binding.etNamaIbu.error = message
                    "pekerjaan" -> binding.etPekerjaan.error = message
                    "gaji" -> binding.etGaji.error = message
                    "noRek" -> binding.etNoRek.error = message
                    "statusRumah" -> binding.etStatusRumah.error = message
                }
            }
        }

        binding.customToolbar.tvTitle.text = "Change Password"

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSaveProfile.isEnabled = !isLoading
            binding.btnSaveProfile.text = if (isLoading) "Menyimpan..." else "Simpan"
        }

        viewModel.successMessage.observe(this) { message ->
            showSnackbar(message ?: "Berhasil", isSuccess = true)
            finish() // balik ke halaman sebelumnya
        }

        viewModel.errorMessage.observe(this) { message ->
            showSnackbar(message ?: "Terjadi kesalahan", isSuccess = false)
        }

        // Ambil data dari intent
        val nama = intent.getStringExtra("EXTRA_NAMA")
        val email = intent.getStringExtra("EXTRA_EMAIL")
        val jenisKelamin = intent.getStringExtra("EXTRA_JENIS_KELAMIN")
        val ttl = intent.getStringExtra("EXTRA_TTL")
        val alamat = intent.getStringExtra("EXTRA_ALAMAT")
        val noTelp = intent.getStringExtra("EXTRA_PHONE")
        val nik = intent.getStringExtra("EXTRA_NIK")
        val namaIbuKandung = intent.getStringExtra("EXTRA_NAMA_IBU")
        val pekerjaan = intent.getStringExtra("EXTRA_PEKERJAAN")
        val gaji = intent.getStringExtra("EXTRA_GAJI")
        val noRek = intent.getStringExtra("EXTRA_NO_REK")
        val statusRumah = intent.getStringExtra("EXTRA_STATUS_RUMAH")

        // Set data ke masing-masing EditText
        binding.etNama.setText(nama)
        binding.etEmail.setText(email)
        binding.etJenisKelamin.setText(jenisKelamin)
        binding.etTtl.setText(ttl)
        binding.etAlamat.setText(alamat)
        binding.etNoTelp.setText(noTelp)
        binding.etNik.setText(nik)
        binding.etNamaIbu.setText(namaIbuKandung)
        binding.etPekerjaan.setText(pekerjaan)
        binding.etNoRek.setText(noRek)
        binding.etStatusRumah.setText(statusRumah)

        // Format dan set gaji saat load data
        if (!gaji.isNullOrEmpty()) {
            println("DEBUG Gaji dari intent: $gaji")
            val formattedGaji = formatRupiahInput(gaji)
            println("DEBUG Gaji setelah format: $formattedGaji")
            binding.etGaji.setText(formattedGaji)
            binding.etGaji.setSelection(formattedGaji.length)
        }

        // TextWatcher untuk auto format Rupiah saat input di etGaji
        binding.etGaji.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return // cegah loop rekursif
                isEditing = true

                val originalString = s.toString()

                // Hapus semua karakter non-digit (kecuali huruf Rp dan tanda baca)
                val cleanString = originalString.replace("[Rp,.\\s]".toRegex(), "")

                // Parse ke Long (bilangan bulat)
                val parsed = cleanString.toLongOrNull() ?: 0L

                // Format ulang ke Rupiah
                val formatted = formatRupiah(parsed)

                // Hanya set text kalau beda dari current (hindari infinite loop)
                if (formatted != current) {
                    current = formatted
                    binding.etGaji.setText(formatted)
                    // Posisikan cursor di akhir teks
                    binding.etGaji.setSelection(formatted.length)
                }

                isEditing = false
            }
        })

        // Tombol Simpan
        binding.btnSaveProfile.setOnClickListener {
            val request = CustomerUpdateProfileRequestDTO(
                ttl = binding.etTtl.text.toString(),
                alamat = binding.etAlamat.text.toString(),
                noTelp = binding.etNoTelp.text.toString(),
                nik = binding.etNik.text.toString(),
                namaIbuKandung = binding.etNamaIbu.text.toString(),
                pekerjaan = binding.etPekerjaan.text.toString(),
                gaji = binding.etGaji.text.toString().replace("[Rp,.\\s]".toRegex(), "").toDoubleOrNull() ?: 0.0,
                noRek = binding.etNoRek.text.toString(),
                statusRumah = binding.etStatusRumah.text.toString()
            )

            viewModel.updateMyProfile(request)
        }
    }
}
