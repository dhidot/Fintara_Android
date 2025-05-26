package com.bcafinance.fintara.ui.repaymentSchedule

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcafinance.fintara.data.viewModel.RepaymentViewModel
import com.bcafinance.fintara.databinding.ActivityRepaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepaymentBinding
    private val viewModel: RepaymentViewModel by viewModels()
    private lateinit var adapter: RepaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val loanRequestId = intent.getStringExtra("loanRequestId")
        Log.d("RepaymentActivity", "loanRequestId: $loanRequestId")
        if (loanRequestId == null) {
            Log.e("RepaymentActivity", "loanRequestId is null!")
            return
        }
        viewModel.fetchRepayments(loanRequestId)

        viewModel.repayments.observe(this) { repayments ->
            adapter.submitList(repayments)
        }
    }

    private fun setupRecyclerView() {
        adapter = RepaymentAdapter()
        binding.recyclerViewRepayments.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRepayments.adapter = adapter
    }
}
