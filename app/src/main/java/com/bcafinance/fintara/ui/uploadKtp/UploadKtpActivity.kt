package com.bcafinance.fintara.ui.uploadKtp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.databinding.ActivityUploadKtpBinding
import com.bcafinance.fintara.fragments.dashboard.AkunFragment
import com.bcafinance.fintara.ui.document.DokumenPribadiActivity
import com.bcafinance.fintara.utils.showSnackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadKtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadKtpBinding
    private lateinit var viewModel: CustomerViewModel
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadKtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init ViewModel
        val customerDao = AppDatabase.getInstance(applicationContext).customerProfileDao()
        val repository = CustomerRepository(RetrofitClient.customerApiService, customerDao)
        val factory = CustomerViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CustomerViewModel::class.java]

        // Observe result
        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess { url ->
                showSnackbar("Upload berhasil: $url", true)
                // Pindah ke DokumenPribadiActivity
                startActivity(Intent(this, DokumenPribadiActivity::class.java))
                finish()
            }.onFailure {
                Log.d("UploadKtpActivity", "Upload failed: ${it.message}")
                showSnackbar("Upload gagal: ${it.message}", false)
                hideLoading()
            }
        }

        // Pick image
        binding.btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Upload
        binding.btnUploadKtp.setOnClickListener {
            selectedImageUri?.let { uri ->
                val file = uriToFile(uri)
                val filePart = createMultipart(file)
                showLoading()
                viewModel.uploadKtp(filePart)
            } ?: Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imageViewKtp.setImageURI(selectedImageUri)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open input stream from URI")

        val tempFile = File.createTempFile("ktp_", ".jpg", cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return tempFile
    }


    private fun createMultipart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    // Menampilkan ProgressBar
    private fun showLoading() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    // Menyembunyikan ProgressBar
    private fun hideLoading() {
        binding.progressBar.visibility = android.view.View.GONE
    }
}
