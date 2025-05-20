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
import com.bcafinance.fintara.databinding.FragmentRejectedHistoryBinding

class RejectedHistoryFragment : Fragment() {

    private var _binding: FragmentRejectedHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var loanViewModel: LoanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRejectedHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = LoanViewModelFactory(LoanRepository(RetrofitClient.loanApiService))
        loanViewModel = ViewModelProvider(this, factory)[LoanViewModel::class.java]

        binding.rvRejectedLoans.layoutManager = LinearLayoutManager(requireContext())

        loanViewModel.isLoadingRejected.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.lottieRejectedLoading.visibility = View.VISIBLE
                binding.lottieRejectedLoading.playAnimation()
                binding.rvRejectedLoans.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.lottieRejectedLoading.visibility = View.GONE
                binding.lottieRejectedLoading.pauseAnimation()

                val list = loanViewModel.rejectedLoans.value
                if (list.isNullOrEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvRejectedLoans.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvRejectedLoans.visibility = View.VISIBLE
                    binding.rvRejectedLoans.adapter = LoanHistoryAdapter(list)
                }
            }
        }

        loanViewModel.rejectedLoans.observe(viewLifecycleOwner) { list ->
            if (!loanViewModel.isLoadingRejected.value!!) {
                if (list.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvRejectedLoans.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvRejectedLoans.visibility = View.VISIBLE
                    binding.rvRejectedLoans.adapter = LoanHistoryAdapter(list)
                }
            }
        }

        loanViewModel.fetchRejectedLoans()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
