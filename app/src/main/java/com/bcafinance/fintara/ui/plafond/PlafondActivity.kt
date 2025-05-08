package com.bcafinance.fintara.ui.plafond

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bcafinance.fintara.data.repository.PlafondRepository
import com.bcafinance.fintara.databinding.ActivityPlafondBinding
import com.bcafinance.fintara.ui.plafond.PlafondAdapter
import com.bcafinance.fintara.data.viewModel.PlafondViewModel
import com.bcafinance.fintara.ui.factory.PlafondViewModelFactory
import com.bcafinance.fintara.utils.showSnackbar

class PlafondActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlafondBinding
    private lateinit var plafondAdapter: PlafondAdapter

    // ViewModel initialization with Factory
    private val plafondViewModel: PlafondViewModel by viewModels {
        PlafondViewModelFactory(PlafondRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlafondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar back button and title
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvTitle.text = "Level Plafond"

        // Set up ViewPager2 for swipeable plafons
        plafondAdapter = PlafondAdapter()
        binding.viewPagerPlafond.adapter = plafondAdapter

        // Observe data from ViewModel
        plafondViewModel.plafonds.observe(this, Observer { plafonds ->
            plafonds?.let {
                plafondAdapter.submitList(it)
            }
        })

        // Observe error message from ViewModel
        plafondViewModel.error.observe(this, Observer { errorMessage ->
            // Show error message (toast, snackbar, etc.)
            errorMessage?.let {
            }
        })

        // Fetch plafonds data from backend
        plafondViewModel.fetchPlafonds()

        // Set static benefit and upgrade instructions
        binding.tvBenefit.text = "Benefit: Dapatkan akses ke lebih banyak fitur dengan setiap level plafon."
        binding.tvUpgrade.text = "How to Upgrade: Untuk naik level, kamu perlu memenuhi persyaratan tertentu dan melakukan upgrade."
    }
}

