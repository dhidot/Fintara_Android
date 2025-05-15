package com.bcafinance.fintara.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.factory.LoanViewModelFactory
import com.bcafinance.fintara.data.repository.LoanRepository
import com.bcafinance.fintara.data.viewModel.LoanViewModel
import com.bcafinance.fintara.ui.loanRequest.LoanRequestActivity
import com.google.android.material.card.MaterialCardView

class AjukanFragment : Fragment(R.layout.fragment_ajukan) {

    private lateinit var viewModel: LoanViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView = view.findViewById<MaterialCardView>(R.id.cardInProgress)
        val tvCardTitle = view.findViewById<TextView>(R.id.tvCardTitle)
        val tvCardContent = view.findViewById<TextView>(R.id.tvCardContent)
        val btnAjukan = view.findViewById<Button>(R.id.btnAjukanSekarang)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        viewModel = ViewModelProvider(
            this,
            LoanViewModelFactory(LoanRepository(RetrofitClient.loanApiService))
        )[LoanViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loan.observe(viewLifecycleOwner) { loan ->
            if (loan != null) {
                cardView.visibility = View.VISIBLE
                btnAjukan.visibility = View.GONE
                tvCardTitle.text = "Pengajuan Sedang Diproses"
                tvCardContent.text = "Jumlah: Rp ${loan.amount}\nTenor: ${loan.tenor} bulan"
            } else {
                cardView.visibility = View.GONE
                btnAjukan.visibility = View.VISIBLE
            }
        }

        btnAjukan.setOnClickListener {
            startActivity(Intent(requireContext(), LoanRequestActivity::class.java))
        }

        viewModel.fetchInProgressLoan()
    }
}


