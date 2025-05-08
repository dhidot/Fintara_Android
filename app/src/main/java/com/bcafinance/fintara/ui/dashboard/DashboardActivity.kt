package com.bcafinance.fintara.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.ActivityDashboardBinding
import com.bcafinance.fintara.fragments.dashboard.AkunFragment
import com.bcafinance.fintara.fragments.dashboard.HomeFragment
import com.bcafinance.fintara.fragments.dashboard.AjukanFragment
import com.bcafinance.fintara.fragments.dashboard.HistoryFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment to HomeFragment
        loadFragment(HomeFragment())

        // Check if navigation to a specific fragment is requested
        intent.getStringExtra("navigate_to")?.let {
            if (it == "ajukan") {
                binding.bottomNavigation.selectedItemId = R.id.menu_ajukan
            }
        }

        // Handle bottom navigation item selection
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_ajukan -> AjukanFragment()
                R.id.menu_akun -> AkunFragment()
                R.id.menu_history -> HistoryFragment()
                else -> HomeFragment()  // Default fallback
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

