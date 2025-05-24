package com.bcafinance.fintara.ui.repaymentSchedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.databinding.ItemRepaymentBinding

class RepaymentAdapter : ListAdapter<RepaymentsScheduleResponse, RepaymentAdapter.ViewHolder>(
    DiffCallback()
) {
    class ViewHolder(private val binding: ItemRepaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RepaymentsScheduleResponse) {
            binding.tvInstallment.text = "Cicilan ke-${item.installmentNumber}"
            binding.tvAmount.text = "Jumlah: Rp${item.amountToPay}"
            binding.tvDueDate.text = "Jatuh Tempo: ${item.dueDate}"

            if (item.paidAt != null) {
                binding.tvStatus.text = "Lunas"
            } else {
                binding.tvStatus.text = if (item.isLate) "Terlambat" else "Belum Lunas"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<RepaymentsScheduleResponse>() {
        override fun areItemsTheSame(oldItem: RepaymentsScheduleResponse, newItem: RepaymentsScheduleResponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RepaymentsScheduleResponse, newItem: RepaymentsScheduleResponse): Boolean =
            oldItem == newItem
    }
}
