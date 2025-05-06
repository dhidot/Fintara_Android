package com.bcafinance.fintara.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.FragmentAkunBinding
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.utils.SessionManager

class AkunFragment : Fragment(R.layout.fragment_akun) {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAkunBinding.bind(view)

        sessionManager = SessionManager(requireContext())

        val isLoggedIn = sessionManager.getToken() != null

        if (isLoggedIn) {
            // tampilkan layout sudah login
            binding.layoutBelumLogin.visibility = View.GONE
            binding.layoutSudahLogin.visibility = View.VISIBLE

            val userName = sessionManager.getUserName() ?: "Pengguna"
            binding.tvNamaUser.text = "Halo, $userName"

            binding.btnLengkapiProfil.setOnClickListener {
                // Arahkan ke halaman detail profil
            }

        } else {
            // tampilkan layout belum login
            binding.layoutBelumLogin.visibility = View.VISIBLE
            binding.layoutSudahLogin.visibility = View.GONE

            binding.btnLoginNow.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
