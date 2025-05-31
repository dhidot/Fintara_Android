package com.bcafinance.fintara.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bcafinance.fintara.MainActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.data.viewModel.LogoutViewModel
import com.bcafinance.fintara.databinding.FragmentAkunBinding
import com.bcafinance.fintara.ui.customer.DetailAkunActivity
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.ui.plafond.PlafondActivity
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.ui.document.DokumenPribadiActivity
import com.bcafinance.fintara.data.factory.LogoutViewModelFactory
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.ui.changePassword.ChangePasswordActivity
import com.bcafinance.fintara.utils.formatRupiah
import com.bcafinance.fintara.utils.showSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AkunFragment : Fragment(R.layout.fragment_akun) {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var logoutViewModel: LogoutViewModel
    private val customerProfileDao by lazy { Room.databaseBuilder(requireContext(), AppDatabase::class.java, "fintara_db").build().customerProfileDao() }
    private val customerRepository by lazy { CustomerRepository(RetrofitClient.customerApiService, customerProfileDao) }
    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(customerRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAkunBinding.bind(view)
        sessionManager = SessionManager(requireContext())
        logoutViewModel = ViewModelProvider(
            this,
            LogoutViewModelFactory(AuthRepository())
        )[LogoutViewModel::class.java]
        if (sessionManager.getToken() != null) {
            showLoggedInUI()
        } else {
            showLoggedOutUI()
        }

        setupButtonActions()
    }

    private fun showLoggedInUI() = with(binding) {
        layoutBelumLogin.visibility = View.GONE
        layoutSudahLogin.visibility = View.VISIBLE
        btnLogout.backgroundTintList = null
        btnLogout.visibility = View.VISIBLE

        val token = sessionManager.getToken()
        if (!token.isNullOrBlank()) {
            val shimmer = binding.shimmerPlafond
            val content = binding.plafondContent


            // Menambahkan shimmer untuk Nama dan Email
            val shimmerNama = binding.shimmerNama
            val shimmerEmail = binding.shimmerEmail
            val contentNama = binding.tvNamaUser
            val contentEmail = binding.tvEmail

            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            content.visibility = View.GONE

            // Menampilkan shimmer untuk Nama dan Email
            shimmerNama.visibility = View.VISIBLE
            shimmerEmail.visibility = View.VISIBLE
            shimmerNama.startShimmer()
            shimmerEmail.startShimmer()

            // Menyembunyikan konten asli sebelum data dimuat
            contentNama.visibility = View.GONE
            contentEmail.visibility = View.GONE

            val userId = sessionManager.getUserId() ?: "default_user_id"
            customerViewModel.fetchProfile(userId)
            Log.d("AkunFragment", "userId dari SessionManager: $userId")


            customerViewModel.profile.observe(viewLifecycleOwner) { profile ->
                // Update Nama dan Email
                contentNama.text = "Halo, ${profile?.name}"
                contentEmail.text = "Email: ${profile?.email}"

                // Matikan shimmer dan tampilkan konten
                shimmerNama.stopShimmer()
                shimmerEmail.stopShimmer()
                shimmerNama.visibility = View.GONE
                shimmerEmail.visibility = View.GONE

                // Tampilkan Nama dan Email
                contentNama.visibility = View.VISIBLE
                contentEmail.visibility = View.VISIBLE

                val plafond = profile?.customerDetails?.plafond
                if (plafond != null) {
                    binding.tvPlafond.text = "Plafond: ${formatRupiah(plafond.maxAmount)}"
                    binding.tvPlafondType.text = "Tipe: ${plafond.name}"

                    val plafondCard = binding.userPlafondCard
                    when (plafond.name) {
                        "Bronze" -> plafondCard.setBackgroundResource(R.drawable.card_bronze)
                        "Silver" -> plafondCard.setBackgroundResource(R.drawable.card_silver)
                        "Gold" -> plafondCard.setBackgroundResource(R.drawable.card_gold)
                        "Platinum" -> plafondCard.setBackgroundResource(R.drawable.card_platinum)
                        else -> plafondCard.setBackgroundResource(R.drawable.card_default)
                    }

                    plafondCard.setOnClickListener {
                        val intent = Intent(requireContext(), PlafondActivity::class.java)
                        startActivity(intent)
                    }

                    // Matikan shimmer dan tampilkan konten plafond
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    content.visibility = View.VISIBLE
                }
            }
        }
        customerViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                requireActivity().showSnackbar(errorMessage, isSuccess = false)
            }
        }



    private fun showLoggedOutUI() = with(binding) {
        layoutBelumLogin.visibility = View.VISIBLE
        layoutSudahLogin.visibility = View.GONE
    }

    private fun setupButtonActions() = with(binding) {
        btnLoginNow.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        itemDetailAkun.setOnClickListener {
            startActivity(Intent(requireContext(), DetailAkunActivity::class.java))
        }

        itemDokumenPribadi.setOnClickListener {
            startActivity(Intent(requireContext(), DokumenPribadiActivity::class.java))
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        itemUbahPassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ -> performLogout() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun performLogout() {
        binding.progressBarLogout.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE

        val sessionManager = SessionManager(requireContext())  // Inisialisasi dengan konteks

        logoutViewModel.logout(
            onSuccess = { message ->
                sessionManager.clearSession() // Hapus session setelah logout berhasil
                requireActivity().showSnackbar(message, isSuccess = true)
                goToHome()
            },
            onError = { errorMessage ->
                requireActivity().showSnackbar(errorMessage, isSuccess = false)
                showLogoutButtonAgain() // Tampilkan kembali tombol logout jika gagal
            }
        )
    }

    private fun showLogoutButtonAgain() = with(binding) {
        progressBarLogout.visibility = View.GONE
        btnLogout.visibility = View.VISIBLE
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
