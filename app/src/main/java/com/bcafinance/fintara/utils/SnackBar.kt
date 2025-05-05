package com.bcafinance.fintara.utils

import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity

// Extension function untuk Activity agar menampilkan Snackbar
fun ComponentActivity.showSnackbar(message: String, isSuccess: Boolean) {
    val snackbar = Snackbar.make(
        findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT
    )

    // Menentukan warna berdasarkan status
    val color = if (isSuccess) {
        ContextCompat.getColor(this, android.R.color.holo_green_light)
    } else {
        ContextCompat.getColor(this, android.R.color.holo_red_light)
    }

    snackbar.setBackgroundTint(color)

    // Menampilkan Snackbar di atas
    val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
    layoutParams.gravity = android.view.Gravity.TOP
    snackbar.view.layoutParams = layoutParams

    snackbar.show()
}
