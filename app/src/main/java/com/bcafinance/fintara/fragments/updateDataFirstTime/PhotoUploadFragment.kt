package com.bcafinance.fintara.fragments.updateDataFirstTime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.FirstLoginViewModel
import com.bcafinance.fintara.databinding.FragmentPhotoUploadBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity
import com.bcafinance.fintara.utils.SessionManager
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.log

class PhotoUploadFragment : Fragment() {

    private var _binding: FragmentPhotoUploadBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private lateinit var ktpUri: Uri
    private lateinit var selfieUri: Uri

    private lateinit var takeKtpPicture: ActivityResultLauncher<Uri>
    private lateinit var takeSelfiePicture: ActivityResultLauncher<Uri>

    private val viewModel: FirstLoginViewModel by activityViewModels()

    private lateinit var customerRepository: CustomerRepository

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Retry launching after permission granted
                pendingPhotoAction?.invoke()
            } else {
                Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    private var pendingPhotoAction: (() -> Unit)? = null

    private fun checkCameraPermissionAndLaunch(action: () -> Unit) {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            action()
        } else {
            pendingPhotoAction = action
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup API service and repository
        val apiService = RetrofitClient.createService(CustomerApiService::class.java)
        customerRepository = CustomerRepository(apiService)

        // Initialize ActivityResultLaunchers for KTP and Selfie
        takeKtpPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel.setKtpUri(ktpUri) // Set KTP URI in ViewModel
                binding.imageKtp.setImageURI(ktpUri)
            }
        }

        takeSelfiePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel.setSelfieUri(selfieUri) // Set Selfie URI in ViewModel
                binding.imageSelfie.setImageURI(selfieUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()
        val textData = viewModel.textData.value


        // Handle capturing KTP photo
        binding.btnCaptureKtp.setOnClickListener {
            checkCameraPermissionAndLaunch {
                ktpUri = createImageUri(requireContext(), "ktp.jpg")
                takeKtpPicture.launch(ktpUri)
            }
        }

        binding.btnCaptureSelfie.setOnClickListener {
            checkCameraPermissionAndLaunch {
                selfieUri = createImageUri(requireContext(), "selfie.jpg")
                takeSelfiePicture.launch(selfieUri)
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (::ktpUri.isInitialized && ::selfieUri.isInitialized) {
                val ktpFile = File(requireContext().cacheDir, "ktp.jpg")
                val selfieFile = File(requireContext().cacheDir, "selfie.jpg")

                if (textData != null) {
                    val request = FirstTimeUpdateRequest(
                        ttl = textData.ttl,
                        alamat = textData.alamat,
                        noTelp = textData.noTelp,
                        nik = textData.nik,
                        namaIbuKandung = textData.namaIbuKandung,
                        pekerjaan = textData.pekerjaan,
                        gaji = textData.gaji,
                        noRek = textData.noRek,
                        statusRumah = textData.statusRumah,
                        ktpPhotoUrl = "", // sementara kosong, nanti diisi setelah dapat dari server
                        selfiePhotoUrl = ""
                    )


                    // 🔍 Tambahkan log di sini
                    Log.d("PhotoUploadFragment", "Token: $token")
                    Log.d("PhotoUploadFragment", "Request: $request")
                    Log.d("PhotoUploadFragment", "KTP file path: ${ktpFile.absolutePath}, exists: ${ktpFile.exists()}, size: ${ktpFile.length()}")
                    Log.d("PhotoUploadFragment", "Selfie file path: ${selfieFile.absolutePath}, exists: ${selfieFile.exists()}, size: ${selfieFile.length()}")

                    lifecycleScope.launch {
                        val result = customerRepository.submitFirstLoginData(token ?: "", request, ktpFile, selfieFile)
                        result.fold(
                            onSuccess = {
                                Toast.makeText(requireContext(), "Data berhasil disubmit", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), DashboardActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            },
                            onFailure = {
                                Log.e("PhotoUploadFragment", "Gagal submit", it)
                                Toast.makeText(requireContext(), "Gagal submit: ${it.message}", Toast.LENGTH_SHORT).show()
                                it.printStackTrace()
                                Log.d("PhotoUploadFragment", "Cause: ${it.cause}")
                                Log.d("PhotoUploadFragment", "LocalizedMessage: ${it.localizedMessage}")
                            }
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Data teks tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Silakan ambil kedua foto terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageUri(context: Context, filename: String): Uri {
        val file = File(context.cacheDir, filename)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
