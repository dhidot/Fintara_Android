package com.bcafinance.fintara.ui.firstTimeUpdate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.fragments.updateDataFirstTime.FirstTimeTextDataFragment
import com.bcafinance.fintara.fragments.updateDataFirstTime.PhotoUploadFragment

class FirstTimeUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_update)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FirstTimeTextDataFragment())
                .commit()
        }
    }

    fun goToPhotoFragment(formData: FirstTimeUpdateRequest) {
        val bundle = Bundle().apply {
            putSerializable("formData", formData)
        }
        val photoFragment = PhotoUploadFragment().apply {
            arguments = bundle
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, photoFragment)
            .addToBackStack(null)
            .commit()
    }
}
