package com.bcafinance.fintara.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.factory.LoanViewModelFactory
import com.bcafinance.fintara.data.repository.LoanRepository
import com.bcafinance.fintara.data.viewModel.LoanViewModel
import com.bcafinance.fintara.databinding.FragmentAjukanBinding
import com.bcafinance.fintara.ui.loanRequest.LoanRequestActivity
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.utils.formatRupiah
import java.math.BigDecimal

class AjukanFragment : Fragment(R.layout.fragment_ajukan) {

    private var _binding: FragmentAjukanBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoanViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAjukanBinding.bind(view)

        viewModel = ViewModelProvider(
            this,
            LoanViewModelFactory(LoanRepository(RetrofitClient.loanApiService))
        )[LoanViewModel::class.java]

        binding.btnAjukanSekarang.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            if (sessionManager.isLoggedIn()) {
                startActivity(Intent(requireContext(), LoanRequestActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        observeViewModel()
        viewModel.fetchInProgressLoan()
    }

    private fun observeViewModel() {
        viewModel.loan.observe(viewLifecycleOwner) { loan ->
            if (loan != null) {
                binding.btnAjukanSekarang.visibility = View.GONE
                binding.loanInfoContainer.visibility = View.VISIBLE

                binding.tvReviewStatus.text = "Pengajuanmu sedang kami proses. Harap menunggu sebentar ya!"
                binding.tvReviewStatus.visibility = View.VISIBLE

                binding.tvLoanAmount.text = "Jumlah Pinjaman: ${formatRupiah(loan.amount)}"
                binding.tvLoanTenor.text = "Tenor: ${loan.tenor} bulan"
                binding.tvLoanInterest.text = "Interest Rate: ${loan.interestRate.multiply(BigDecimal(100))}%"
                binding.tvLoanInterestAmount.text = "Jumlah Bunga: ${formatRupiah(loan.interestAmount)}"
                binding.tvLoanStatus.text = "Status Pengajuan: ${loan.status}"
                binding.tvBranchName.text = "Nama Cabang: ${loan.branchName}"
                binding.tvMarketingName.text = "Nama Marketer: ${loan.marketingName}"
            } else {
                binding.btnAjukanSekarang.visibility = View.VISIBLE
                binding.loanInfoContainer.visibility = View.GONE
                binding.tvReviewStatus.text = "Anda tidak memiliki pengajuan."
                binding.tvReviewStatus.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingAnimation.visibility = View.VISIBLE
                binding.btnAjukanSekarang.visibility = View.GONE
                binding.loanInfoContainer.visibility = View.GONE
                binding.tvReviewStatus.visibility = View.GONE
            } else {
                binding.loadingAnimation.visibility = View.GONE
                binding.btnAjukanSekarang.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchInProgressLoan()
    }
}
