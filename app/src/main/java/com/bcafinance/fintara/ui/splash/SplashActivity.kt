package com.bcafinance.fintara.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bcafinance.fintara.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Pasang splash bawaan Android 12+
        installSplashScreen()

        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
