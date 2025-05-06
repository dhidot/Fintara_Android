package com.bcafinance.fintara.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.ActivityDashboardBinding
import com.bcafinance.fintara.fragments.AkunFragment
import com.bcafinance.fintara.fragments.HomeFragment
import com.bcafinance.fintara.fragments.AjukanFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        replaceFragment(HomeFragment())

        val destination = intent.getStringExtra("navigate_to")
        if (destination == "ajukan") {
            binding.bottomNavigation.selectedItemId = R.id.menu_ajukan
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> replaceFragment(HomeFragment())
                R.id.menu_ajukan -> replaceFragment(AjukanFragment())
                R.id.menu_akun -> replaceFragment(AkunFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
