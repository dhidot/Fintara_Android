package com.bcafinance.fintara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcafinance.fintara.databinding.ItemLoanHistoryBinding
import com.bcafinance.fintara.data.model.dto.loan.LoanHistoryResponse
import com.bcafinance.fintara.utils.formatRupiah

class LoanHistoryAdapter(
    private val items: List<LoanHistoryResponse>
) : RecyclerView.Adapter<LoanHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLoanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoanHistoryResponse) = with(binding) {
            tvStatus.text = item.status
            tvStatus.setTextColor(getStatusColor(item.status))

            tvAmount.text = "Amount Pinjaman : ${formatRupiah(item.amount)}"
            tvDate.text = "Tanggal Pengajuan : ${item.createdAt.take(10)}"
            tvTenor.text = "Tenor : ${item.tenor} bulan"
            tvInterestAmount.text = "Bunga : ${formatRupiah(item.interestAmount)}"
            tvDisbursedAmount.text = "Pencairan : ${formatRupiah(item.disbursedAmount)}"

            root.setOnClickListener {
                if (item.status.uppercase() == "DISBURSED") {
                    val context = root.context
                    val intent = android.content.Intent(context, com.bcafinance.fintara.ui.repaymentSchedule.RepaymentActivity::class.java)
                    intent.putExtra("loanRequestId", item.id) // Pastikan `id` ini UUID dari loan request
                    context.startActivity(intent)
                }
            }
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

    private fun getStatusColor(status: String): Int {
        return when (status.uppercase()) {
            "DISBURSED" -> android.graphics.Color.parseColor("#4CAF50")
            "DITOLAK_MARKETING" -> android.graphics.Color.RED
            "DITOLAK_BM" -> android.graphics.Color.RED
            "NOT_DISBURSED" -> android.graphics.Color.RED
            "REJECTED" -> android.graphics.Color.RED
            else -> android.graphics.Color.GRAY
        }
    }

}
