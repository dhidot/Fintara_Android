package com.bcafinance.fintara.fragments.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcafinance.fintara.adapter.LoanHistoryAdapter
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.factory.LoanViewModelFactory
import com.bcafinance.fintara.data.repository.LoanRepository
import com.bcafinance.fintara.data.viewModel.LoanViewModel
import com.bcafinance.fintara.databinding.FragmentApprovedHistoryBinding

class ApprovedHistoryFragment : Fragment() {

    private var _binding: FragmentApprovedHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var loanViewModel: LoanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApprovedHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = LoanViewModelFactory(LoanRepository(RetrofitClient.loanApiService))
        loanViewModel = ViewModelProvider(this, factory)[LoanViewModel::class.java]

        binding.rvApprovedLoans.layoutManager = LinearLayoutManager(requireContext())

        loanViewModel.isLoadingApproved.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.lottieLoading.visibility = View.VISIBLE
                binding.lottieLoading.playAnimation()
                binding.rvApprovedLoans.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.lottieLoading.visibility = View.GONE
                binding.lottieLoading.pauseAnimation()

                val list = loanViewModel.approvedLoans.value
                if (list.isNullOrEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvApprovedLoans.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvApprovedLoans.visibility = View.VISIBLE
                    binding.rvApprovedLoans.adapter = LoanHistoryAdapter(list)
                }
            }
        }

        // Inisialisasi juga observer approvedLoans untuk update data di saat isLoading=false
        loanViewModel.approvedLoans.observe(viewLifecycleOwner) { list ->
            if (!loanViewModel.isLoadingApproved.value!!) {
                if (list.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvApprovedLoans.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvApprovedLoans.visibility = View.VISIBLE
                    binding.rvApprovedLoans.adapter = LoanHistoryAdapter(list)
                }
            }
        }

        loanViewModel.fetchApprovedLoans()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
