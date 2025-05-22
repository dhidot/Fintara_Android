package com.bcafinance.fintara.ui.changePassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bcafinance.fintara.data.factory.ChangePasswordViewModelFactory
import com.bcafinance.fintara.data.model.dto.customer.ChangePasswordRequest
import com.bcafinance.fintara.data.repository.AuthRepository
import com.bcafinance.fintara.data.viewModel.ChangePasswordViewModel
import com.bcafinance.fintara.databinding.ActivityChangePasswordBinding
import com.bcafinance.fintara.ui.dashboard.DashboardActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AuthRepository()
        val factory = ChangePasswordViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ChangePasswordViewModel::class.java]

        binding.btnChangePassword.setOnClickListener {
            val request = ChangePasswordRequest(
                binding.etOldPassword.text.toString(),
                binding.etNewPassword.text.toString(),
                binding.etConfirmPassword.text.toString()
            )
            viewModel.changePassword(request)
        }

        viewModel.changePasswordResult.observe(this) { result ->
            result.onSuccess {
                Log.d("ChangePassword", "Berhasil ubah password: $it")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("navigate_to", "akun")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            result.onFailure {
                Log.e("ChangePassword", "Gagal ubah password", it)
                Toast.makeText(this, it.message ?: "Gagal mengubah password", Toast.LENGTH_LONG).show()
            }
        }

    }
}
