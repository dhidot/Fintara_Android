package com.bcafinance.fintara.ui.document

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.ActivityDokumenBinding
import com.bcafinance.fintara.databinding.ActivityPlafondBinding

class DokumenPribadiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDokumenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dokumen)

        binding = ActivityDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set MaterialToolbar as ActionBar
        setSupportActionBar(binding.customToolbar.toolbar)
        binding.customToolbar.tvTitle.text = "Dokumen Pribadi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
}