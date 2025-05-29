package com.bcafinance.fintara.ui.repaymentSchedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.View

import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.databinding.ItemRepaymentBinding
import com.bcafinance.fintara.utils.formatRupiah
import com.bcafinance.fintara.R

class RepaymentAdapter : ListAdapter<RepaymentsScheduleResponse, RepaymentAdapter.ViewHolder>(
    DiffCallback()
) {

    var onPayClick: ((RepaymentsScheduleResponse) -> Unit)? = null

    class ViewHolder(private val binding: ItemRepaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RepaymentsScheduleResponse, onPayClick: ((RepaymentsScheduleResponse) -> Unit)?) {
            binding.tvInstallment.text = "Cicilan ke-${item.installmentNumber}"
            binding.tvAmountToPay.text = "Jumlah Tagihan: ${formatRupiah(item.amountToPay)}"
            binding.tvAmountPaid.text = "Jumlah Dibayar: ${formatRupiah(item.amountPaid)}"
            binding.tvPenaltyAmount.text = "Denda: ${formatRupiah(item.penaltyAmount)}"
            binding.tvDueDate.text = "Jatuh Tempo: ${item.dueDate}"
            binding.tvPaidAt.text = "Dibayar Pada: ${item.paidAt ?: "-"}"

            if (item.paidAt != null) {
                binding.layoutRoot.setBackgroundResource(R.drawable.bg_card_rounded_green)
                binding.btnPay.visibility = View.GONE
            } else {
                binding.layoutRoot.setBackgroundResource(R.drawable.bg_card_rounded_red)
                binding.btnPay.visibility = View.VISIBLE
            }

            binding.btnPay.setOnClickListener {
                onPayClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onPayClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<RepaymentsScheduleResponse>() {
        override fun areItemsTheSame(oldItem: RepaymentsScheduleResponse, newItem: RepaymentsScheduleResponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RepaymentsScheduleResponse, newItem: RepaymentsScheduleResponse): Boolean =
            oldItem == newItem
    }
}

