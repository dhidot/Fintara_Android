package com.bcafinance.fintara.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.FragmentStatusBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bcafinance.fintara.ui.login.LoginActivity
import com.bcafinance.fintara.ui.register.RegisterActivity

class StatusBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentStatusBottomSheetBinding? = null
    private val binding get() = _binding!!

    interface OnStatusActionListener {
        fun onSuccessAction()
        fun onErrorDismiss()
    }

    private var listener: OnStatusActionListener? = null

    fun setOnStatusActionListener(listener: OnStatusActionListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatusBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val isSuccess = arguments?.getBoolean(ARG_SUCCESS) ?: false
        val message = arguments?.getString(ARG_MESSAGE) ?: ""

        binding.lottieStatus.setAnimation(
            if (isSuccess) R.raw.success_check else R.raw.error_cross
        )
        binding.tvStatusMessage.text = message

        binding.btnClose.text = if (isSuccess) "Kembali ke Login" else "Tutup"

        binding.btnClose.setOnClickListener {
            if (isSuccess) {
                listener?.onSuccessAction()
            } else {
                listener?.onErrorDismiss()
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SUCCESS = "isSuccess"
        private const val ARG_MESSAGE = "message"

        fun newInstance(isSuccess: Boolean, message: String): StatusBottomSheetDialogFragment {
            val fragment = StatusBottomSheetDialogFragment()
            val args = Bundle()
            args.putBoolean(ARG_SUCCESS, isSuccess)
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }
}

