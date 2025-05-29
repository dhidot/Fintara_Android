package com.bcafinance.fintara.ui.repaymentSchedule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.data.viewModel.PaymentViewModel
import com.bcafinance.fintara.data.viewModel.RepaymentViewModel
import com.bcafinance.fintara.databinding.ActivityRepaymentBinding
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepaymentBinding
    private val repaymentViewModel: RepaymentViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()
    private lateinit var adapter: RepaymentAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("RepaymentActivity", "Permission granted")
            } else {
                Log.e("RepaymentActivity", "Permission denied")
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        checkPermissions()

        val loanRequestId = intent.getStringExtra("loanRequestId")
        Log.d("RepaymentActivity", "loanRequestId: $loanRequestId")
        if (loanRequestId == null) {
            Log.e("RepaymentActivity", "loanRequestId is null!")
            return
        }
        repaymentViewModel.fetchRepayments(loanRequestId)

        repaymentViewModel.repayments.observe(this) { repayments ->
            adapter.submitList(repayments)
        }

        paymentViewModel.snapToken.observe(this) { token ->
            if (token != null) {
                Log.d("RepaymentActivity", "Received Snap Token: $token")
                startMidtransPayment(token)
            } else {
                Log.e("RepaymentActivity", "Failed to get Snap Token")
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RepaymentAdapter()
        binding.recyclerViewRepayments.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRepayments.adapter = adapter

        adapter.onPayClick = { repayment ->
            startPayment(repayment)
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    private fun startPayment(repayment: RepaymentsScheduleResponse) {
        val repaymentScheduleId = repayment.id
        val amount = repayment.amountToPay.toLong()

        Log.d("RepaymentActivity", "Start payment for repaymentId: $repaymentScheduleId amount: $amount")
        paymentViewModel.fetchSnapToken(repaymentScheduleId, amount)
    }

    private fun startMidtransPayment(snapToken: String) {
        val sdkUIFlow = SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-mhvep2-CtMyql_Wy")  // Ganti dengan client key Midtrans kamu
            .setContext(this)
            .setTransactionFinishedCallback { result: TransactionResult ->
                Log.d("MidtransPayment", "Transaction finished with status: ${result.statusMessage}")
            }
            .setMerchantBaseUrl("https://915d-2001-448a-2061-c117-b8b1-dd8c-c5d5-a95a.ngrok-free.app/api/v1/payments/") // Ganti dengan URL backend kamu
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

        sdkUIFlow.startPaymentUiFlow(this, snapToken)
    }

}
