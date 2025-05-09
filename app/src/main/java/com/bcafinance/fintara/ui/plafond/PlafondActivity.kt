package com.bcafinance.fintara.ui.plafond

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bcafinance.fintara.data.repository.PlafondRepository
import com.bcafinance.fintara.databinding.ActivityPlafondBinding
import com.bcafinance.fintara.data.viewModel.PlafondViewModel
import com.bcafinance.fintara.ui.factory.PlafondViewModelFactory

class PlafondActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlafondBinding
    private lateinit var plafondAdapter: PlafondAdapter

    private val plafondViewModel: PlafondViewModel by viewModels {
        PlafondViewModelFactory(PlafondRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlafondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set MaterialToolbar as ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle back button click
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup ViewPager2 adapter
        plafondAdapter = PlafondAdapter()
        binding.viewPagerPlafond.adapter = plafondAdapter

        // Observe plafonds from ViewModel
        plafondViewModel.plafonds.observe(this, Observer { plafonds ->
            plafonds?.let {
                plafondAdapter.submitList(it)
            }
        })

        // Observe errors (you can show snackbar, toast, etc.)
        plafondViewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
            }
        })

        // Trigger data fetch
        plafondViewModel.fetchPlafonds()

        // Static text
        binding.tvBenefit.text = "Benefit: Dapatkan akses ke lebih banyak fitur dengan setiap level plafon."
        binding.tvUpgrade.text = "How to Upgrade: Untuk naik level, kamu perlu memenuhi persyaratan tertentu dan melakukan upgrade."
    }
}
