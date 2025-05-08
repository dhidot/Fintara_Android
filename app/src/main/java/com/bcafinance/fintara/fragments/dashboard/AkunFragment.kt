package com.bcafinance.fintara.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bcafinance.fintara.MainActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.data.viewModel.LogoutViewModel
import com.bcafinance.fintara.databinding.FragmentAkunBinding
import com.bcafinance.fintara.ui.customer.DetailAkunActivity
import com.bcafinance.fintara.ui.factory.CustomerViewModelFactory
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.ui.plafond.PlafondActivity
import com.bcafinance.fintara.utils.SessionManager
import com.bcafinance.fintara.utils.showSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AkunFragment : Fragment(R.layout.fragment_akun) {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val logoutViewModel: LogoutViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(CustomerRepository(RetrofitClient.customerService))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAkunBinding.bind(view)
        sessionManager = SessionManager(requireContext())

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
            customerViewModel.fetchProfile(token)

            customerViewModel.profile.observe(viewLifecycleOwner) { profile ->
                tvNamaUser.text = "Halo, ${profile?.name}"
                tvEmail.text = "Email: ${profile?.email}"

                val plafond = profile?.customerDetails?.plafond
                if (plafond != null) {
                    tvPlafond.text = "Plafond: Rp${plafond?.maxAmount}"
                    tvPlafondType.text = "Tipe: ${plafond?.name}"

                    // Set the color of the card based on the plafond type
                    val plafondCard = binding.userPlafondCard
                    when (plafond.name) {
                        "Bronze" -> plafondCard.setBackgroundResource(R.drawable.card_bronze)
                        "Silver" -> plafondCard.setBackgroundResource(R.drawable.card_silver)
                        "Gold" -> plafondCard.setBackgroundResource(R.drawable.card_gold)
                        "Platinum" -> plafondCard.setBackgroundResource(R.drawable.card_platinum)
                        else -> plafondCard.setBackgroundResource(R.drawable.card_default)
                    }

                    // Add onClickListener here
                    plafondCard.setOnClickListener {
                        Log.d("AkunFragment", "Plafond card clicked")
                        val intent = Intent(requireContext(), PlafondActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            customerViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                requireActivity().showSnackbar(errorMessage, isSuccess = false)
            }
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

        tvDetailAkun.setOnClickListener {
            startActivity(Intent(requireContext(), DetailAkunActivity::class.java))
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
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

        sessionManager.getToken()?.let { token ->
            logoutViewModel.logout(
                token,
                onSuccess = { message ->
                    sessionManager.clearSession()
                    requireActivity().showSnackbar(message, isSuccess = true)
                    goToHome()
                },
                onError = { errorMessage ->
                    requireActivity().showSnackbar(errorMessage, isSuccess = false)
                    showLogoutButtonAgain()
                }
            )
        } ?: run {
            requireActivity().showSnackbar("Token tidak ditemukan", isSuccess = false)
            showLogoutButtonAgain()
        }
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
