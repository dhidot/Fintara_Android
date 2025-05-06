package com.bcafinance.fintara.ui.pengajuan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.databinding.ActivitySimulasiBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity
import com.bcafinance.fintara.utils.showSnackbar

class SimulasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimulasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimulasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAjukan.setOnClickListener {
            val jumlah = binding.etJumlahPinjaman.text.toString()
            val tenor = binding.etTenor.text.toString()

            if (jumlah.isNotBlank() && tenor.isNotBlank()) {
                // Simulasi valid, arahkan ke AjukanFragment
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("navigate_to", "ajukan") // nanti AjukanFragment akan dibuka
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else {
                showSnackbar("Mohon isi semua data", false)
            }
        }
    }
}