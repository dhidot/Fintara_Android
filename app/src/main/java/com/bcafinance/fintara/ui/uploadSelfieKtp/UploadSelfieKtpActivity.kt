package com.bcafinance.fintara.ui.uploadSelfieKtp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.databinding.ActivityUploadSelfieKtpBinding
import com.bcafinance.fintara.ui.document.DokumenPribadiActivity
import com.bcafinance.fintara.ui.uploadKtp.UploadKtpActivity
import com.bcafinance.fintara.utils.showSnackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadSelfieKtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadSelfieKtpBinding
    private lateinit var viewModel: CustomerViewModel
    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
        private const val CAPTURE_IMAGE_REQUEST = 101
        private const val REQUEST_CODE_PERMISSION = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadSelfieKtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init ViewModel
        val customerDao = AppDatabase.getInstance(applicationContext).customerProfileDao()
        val repository = CustomerRepository(RetrofitClient.customerApiService, customerDao)
        val factory = CustomerViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CustomerViewModel::class.java]

        // Observe result
        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess { url ->
                showSnackbar("Upload berhasil", true)
                lifecycleScope.launch {
                    hideLoading()
                    delay(2000)
                    val intent = Intent(this@UploadSelfieKtpActivity, DokumenPribadiActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                }
            }.onFailure {
                Log.e("UploadSelfieKtpActivity", "Upload gagal: ${it.message}")
                showSnackbar("Upload gagal: ${it.message}", false)
                hideLoading()
            }
        }

        // Pick image
        binding.btnPickImage.setOnClickListener {
            showImageSourceDialog()
        }

        // Upload
        binding.btnUploadSelfieKtp.setOnClickListener {
            selectedImageUri?.let { uri ->
                try {
                    val file = uriToFile(uri)
                    val filePart = createMultipart(file)
                    showLoading()
                    viewModel.uploadSelfieKtp(filePart)
                } catch (e: Exception) {
                    Log.e("UploadSelfieKtpActivity", "Gagal menyiapkan file: ${e.message}")
                    showSnackbar("Gagal membaca gambar", false)
                }
            } ?: Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                }
                CAPTURE_IMAGE_REQUEST -> {
                    selectedImageUri = photoUri
                }
            }
            selectedImageUri?.let {
                binding.imageViewSelfieKtp.setImageURI(it)
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkPermissionAndOpenCamera()
                    1 -> checkPermissionAndOpenGallery()
                }
            }
            .show()
    }

    private fun checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Untuk Android 13 ke atas
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_PERMISSION)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_PERMISSION)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val photoFile = File.createTempFile("selfie_ktp_photo_", ".jpg", cacheDir)
        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider", // pastikan fileprovider ini sesuai
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }

    private fun uriToFile(uri: Uri): File {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Tidak bisa buka gambar")

            val tempFile = File.createTempFile("selfie_ktp", ".jpg", cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            tempFile
        } catch (e: Exception) {
            throw RuntimeException("Gagal mengkonversi uri ke file: ${e.localizedMessage}")
        }
    }

    private fun createMultipart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty()) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val permission = permissions[0]

            when (permission) {
                android.Manifest.permission.CAMERA -> {
                    if (granted) {
                        openCamera()
                    } else {
                        Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
                    }
                }
                android.Manifest.permission.READ_MEDIA_IMAGES -> {
                    if (granted) {
                        openGallery()
                    } else {
                        Toast.makeText(this, "Izin galeri ditolak", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
