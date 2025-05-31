package com.bcafinance.fintara.ui.loanRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.dto.loan.LoanPreviewResponse
import com.bcafinance.fintara.utils.formatRupiah
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.Serializable
import java.math.BigDecimal

class LoanPreviewBottomSheet : BottomSheetDialogFragment() {

    private var data: LoanPreviewResponse? = null
    private var onConfirm: (() -> Unit)? = null

    companion object {
        fun newInstance(data: LoanPreviewResponse, onConfirm: () -> Unit): LoanPreviewBottomSheet {
            val fragment = LoanPreviewBottomSheet()
            fragment.data = data
            fragment.onConfirm = onConfirm
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottomsheet_loan_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preview = data ?: return
        val interestRatePercentage = preview.interestRate.multiply(BigDecimal(100))

        view.findViewById<TextView>(R.id.tvRequestedAmount).text = formatRupiah(preview.requestedAmount)
        view.findViewById<TextView>(R.id.tvDisbursedAmount).text = formatRupiah(preview.disbursedAmount)
        view.findViewById<TextView>(R.id.tvTenor).text = "${preview.tenor} bulan"
        view.findViewById<TextView>(R.id.tvInterestRate).text = "${interestRatePercentage}%"
        view.findViewById<TextView>(R.id.tvInterestAmount).text = formatRupiah(preview.interestAmount)
        view.findViewById<TextView>(R.id.tvFeesAmount).text = formatRupiah(preview.feesAmount)
        view.findViewById<TextView>(R.id.tvTotalRepayment).text = formatRupiah(preview.totalRepayment)
        view.findViewById<TextView>(R.id.tvEstimatedInstallment).text = formatRupiah(preview.estimatedInstallment)

        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            dismiss()
            onConfirm?.invoke()
        }
    }
}
