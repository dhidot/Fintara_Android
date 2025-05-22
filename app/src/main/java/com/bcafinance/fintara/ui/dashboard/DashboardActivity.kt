package com.bcafinance.fintara.ui.dashboard

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.SessionManager
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

        // Cek apakah user sudah login
        val isLoggedIn = SessionManager(this).isLoggedIn()
        if (!isLoggedIn) {
            binding.bottomNavigation.menu.removeItem(R.id.menu_history)
        }

        // Set default fragment ke HomeFragment
        loadFragment(HomeFragment())

        requestpermissionNotification()

        // Cek jika ada navigasi khusus dari intent
        intent.getStringExtra("navigate_to")?.let {
            val menuId = when (it) {
                "ajukan" -> R.id.menu_ajukan
                "akun" -> R.id.menu_akun
                else -> R.id.menu_home
            }
            binding.bottomNavigation.selectedItemId = menuId
        }

        binding.bottomNavigation.itemRippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
        binding.bottomNavigation.itemBackground = null

        // Listener BottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_ajukan -> AjukanFragment()
                R.id.menu_akun -> AkunFragment()
                R.id.menu_history -> HistoryFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }

        // Permission notifikasi (untuk Android 13 ke atas)
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

