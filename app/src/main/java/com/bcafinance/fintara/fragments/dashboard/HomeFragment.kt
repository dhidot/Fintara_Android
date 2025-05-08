package com.bcafinance.fintara.fragments.dashboard

import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bcafinance.fintara.databinding.FragmentHomeBinding
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.ui.pengajuan.SimulasiActivity
import com.bcafinance.fintara.utils.SessionManager

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        session = SessionManager(requireContext())

        binding.btnAjukanPromo.setOnClickListener {
            if (session.isLoggedIn()) {
                startActivity(Intent(requireContext(), SimulasiActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}