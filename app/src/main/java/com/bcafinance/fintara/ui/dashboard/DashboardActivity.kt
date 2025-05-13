package com.bcafinance.fintara.ui.dashboard

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        requestpermissionNotification()

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun requestpermissionNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("FCM", "Permission granted")
            } else {
                Log.e("FCM", "Permission denied")
            }
        }
    }
}

