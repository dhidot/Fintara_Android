package com.bcafinance.fintara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcafinance.fintara.databinding.ItemLoanHistoryBinding
import com.bcafinance.fintara.data.model.dto.loan.LoanHistoryResponse

class LoanHistoryAdapter(
    private val items: List<LoanHistoryResponse>
) : RecyclerView.Adapter<LoanHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLoanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoanHistoryResponse) {
//            binding.tvPlafondName.text = item.plafondName
            binding.tvAmount.text = "Rp${item.amount}"
            binding.tvStatus.text = item.status
            binding.tvDate.text = item.createdAt.take(10) // yyyy-MM-dd
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoanHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
