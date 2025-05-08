package com.bcafinance.fintara.ui.plafond

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.ItemPlafondBinding
import com.bcafinance.fintara.data.model.Plafond
import com.bcafinance.fintara.utils.toLocaleFormat

class PlafondAdapter : RecyclerView.Adapter<PlafondAdapter.PlafondViewHolder>() {

    private val plafonds = mutableListOf<Plafond>()

    // Update the method to accept a list of Plafond objects
    fun submitList(list: List<Plafond>) {
        plafonds.clear()
        plafonds.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlafondViewHolder {
        val binding = ItemPlafondBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlafondViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlafondViewHolder, position: Int) {
        holder.bind(plafonds[position])
    }

    override fun getItemCount(): Int = plafonds.size

    class PlafondViewHolder(private val binding: ItemPlafondBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plafond: Plafond) {
            binding.tvPlafond.text = plafond.name
            binding.tvMaxAmount.text = "Maksimal: Rp ${plafond.maxAmount?.toInt()?.toLocaleFormat()}"
            binding.tvInterestRate.text = "Bunga: ${(plafond.interestRate ?: 0.0) * 100}%"
            binding.tvTenor.text = "Tenor: ${plafond.minTenor} - ${plafond.maxTenor} bulan"

            // Set the icon based on plafon level
            when (plafond.name) {
                "Bronze" -> binding.ivPlafondIcon.setImageResource(R.drawable.bronze)
                "Silver" -> binding.ivPlafondIcon.setImageResource(R.drawable.silver)
                "Gold" -> binding.ivPlafondIcon.setImageResource(R.drawable.gold)
                "Platinum" -> binding.ivPlafondIcon.setImageResource(R.drawable.platinum)
                else -> binding.ivPlafondIcon.setImageResource(R.drawable.bronze) // default to bronze if unknown
            }
        }
    }
}

