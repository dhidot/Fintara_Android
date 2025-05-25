package com.bcafinance.fintara.ui.plafond

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.repository.PlafondRepository
import com.bcafinance.fintara.databinding.ActivityPlafondBinding
import com.bcafinance.fintara.data.viewModel.PlafondViewModel
import com.bcafinance.fintara.data.factory.PlafondViewModelFactory

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
        setSupportActionBar(binding.customToolbar.toolbar)
        binding.customToolbar.tvTitle.text = "Level Plafond"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup ViewPager2 adapter
        plafondAdapter = PlafondAdapter()
        binding.viewPagerPlafond.adapter = plafondAdapter

        setupViewPager()

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Kembali ke activity sebelumnya saat tombol back ditekan
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPagerPlafond

        // Pastikan clipToPadding & clipChildren false agar item bisa muncul di padding
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3

        // Set padding start & end agar ada ruang sneak peek
        val paddingPx = resources.getDimensionPixelOffset(R.dimen.page_offset)
        viewPager.setPadding(paddingPx, 0, paddingPx, 0)

        // Matikan clip di RecyclerView internal ViewPager2
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false

        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.page_margin)

        viewPager.setPageTransformer { page, position ->
            val offset = position * -(2 * paddingPx + pageMarginPx)
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }

            // Scale effect untuk item tengah (lebih besar)
            val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
            page.scaleY = scale
            page.scaleX = scale
        }
    }

}
