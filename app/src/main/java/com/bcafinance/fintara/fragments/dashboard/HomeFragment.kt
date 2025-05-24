package com.bcafinance.fintara.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bcafinance.fintara.R
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.factory.CustomerViewModelFactory
import com.bcafinance.fintara.data.factory.LoanViewModelFactory
import com.bcafinance.fintara.data.factory.PlafondViewModelFactory
import com.bcafinance.fintara.data.model.Plafond
import com.bcafinance.fintara.data.model.dto.loan.LoanSimulationRequest
import com.bcafinance.fintara.data.model.room.AppDatabase
import com.bcafinance.fintara.data.repository.CustomerRepository
import com.bcafinance.fintara.data.repository.LoanRepository
import com.bcafinance.fintara.data.repository.PlafondRepository
import com.bcafinance.fintara.data.viewModel.CustomerViewModel
import com.bcafinance.fintara.data.viewModel.LoanViewModel
import com.bcafinance.fintara.data.viewModel.PlafondViewModel
import com.bcafinance.fintara.databinding.FragmentHomeBinding
import com.bcafinance.fintara.ui.loanRequest.LoanRequestActivity
import com.bcafinance.fintara.ui.login.LoginActivity
import com.facebook.shimmer.ShimmerFrameLayout
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private lateinit var loanViewModel: LoanViewModel
    private lateinit var plafondViewModel: PlafondViewModel

    private val customerProfileDao by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            "fintara_db"
        ).build().customerProfileDao()
    }

    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(CustomerRepository(RetrofitClient.customerApiService, customerProfileDao))
    }

    private val handler = Handler(Looper.getMainLooper())
    private var runnableJumlahPinjaman: Runnable? = null
    private var runnableTenor: Runnable? = null
    private val debounceDelay = 700L

    private var currentAmountSteps = 0
    private var currentTenor = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        session = SessionManager(requireContext())

        // LoanViewModel
        val repository = LoanRepository(RetrofitClient.loanApiService)
        loanViewModel = ViewModelProvider(this, LoanViewModelFactory(repository))[LoanViewModel::class.java]

        // PlafondViewModel
        plafondViewModel = ViewModelProvider(this, PlafondViewModelFactory(PlafondRepository()))[PlafondViewModel::class.java]

        // Setup Ajukan Sekarang button
        setupAjukanSekarangButton()

        // Observe plafonds
        plafondViewModel.plafonds.observe(viewLifecycleOwner) { list ->
            val bronze = list.find { it.name.equals("Bronze", ignoreCase = true) }
            if (bronze != null) {
                setupSimulasiSeekBars(bronze)
            } else {
                Log.e("HomeFragment", "Plafon Bronze tidak ditemukan.")
            }
        }

        plafondViewModel.error.observe(viewLifecycleOwner) {
            Log.e("HomeFragment", "Error fetching plafonds: $it")
        }

        plafondViewModel.fetchPlafonds()

        // Cek login dan tampilkan nama customer
        if (session.isLoggedIn()) {
            binding.layoutFeatures.visibility = View.GONE
            binding.HeroSection.visibility = View.GONE
            binding.FeedbackSection.visibility = View.GONE
            binding.LoggedInSection.visibility = View.VISIBLE

            // Tampilkan shimmer & sembunyikan nama dulu
            binding.shimmerGreeting.visibility = View.VISIBLE
            binding.tvGreeting.visibility = View.GONE

            // Observe profile
            customerViewModel.profile.observe(viewLifecycleOwner) { customer ->
                // Matikan shimmer, tampilkan nama
                binding.shimmerGreeting.visibility = View.GONE
                binding.tvGreeting.visibility = View.VISIBLE

                if (customer != null) {
                    binding.tvGreeting.text = "${customer.name}!"
                } else {
                    binding.tvGreeting.text = "Pengguna!"
                }
            }

            customerViewModel.fetchProfile(userId = session.getUserId() ?: "")
        } else {
            // Tampilkan langsung greeting default
            binding.tvGreetingLabel.visibility = View.VISIBLE
            binding.tvGreeting.visibility = View.VISIBLE
            binding.shimmerGreeting.visibility = View.GONE
            binding.tvGreeting.text = "Pengguna!"
        }
    }

    private fun setupAjukanSekarangButton() {
        binding.btnApplyNow.setOnClickListener {
            val targetActivity = if (session.isLoggedIn()) LoanRequestActivity::class.java else LoginActivity::class.java
            startActivity(Intent(requireContext(), targetActivity))
        }
    }

    private fun setupSimulasiSeekBars(plafond: Plafond) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val stepAmount = 500_000
        val maxAmountSteps = plafond.maxAmount.toInt() / stepAmount

        val minTenor = plafond.minTenor
        val maxTenor = plafond.maxTenor
        val tenorRange = maxTenor - minTenor

        // Setup SeekBar
        currentAmountSteps = 0
        currentTenor = 0
        binding.seekJumlahPinjaman.max = maxAmountSteps
        binding.seekTenor.max = tenorRange
        binding.seekJumlahPinjaman.progress = currentAmountSteps
        binding.seekTenor.progress = currentTenor

        updateSimulasiText(formatter, stepAmount, minTenor, currentAmountSteps, currentTenor)

        loanViewModel.simulateLoan(LoanSimulationRequest(0.toBigDecimal(), minTenor))

        fun simulateLoanWithDebounce(amountSteps: Int, tenorProgress: Int) {
            runnableJumlahPinjaman?.let { handler.removeCallbacks(it) }
            runnableTenor?.let { handler.removeCallbacks(it) }

            runnableJumlahPinjaman = Runnable {
                updateSimulasiText(formatter, stepAmount, minTenor, amountSteps, tenorProgress)

                val amount = amountSteps * stepAmount
                val tenor = minTenor + tenorProgress
                loanViewModel.simulateLoan(LoanSimulationRequest(amount.toBigDecimal(), tenor))
            }

            handler.postDelayed(runnableJumlahPinjaman!!, debounceDelay)
        }

        binding.seekJumlahPinjaman.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                simulateLoanWithDebounce(progress, binding.seekTenor.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekTenor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                simulateLoanWithDebounce(binding.seekJumlahPinjaman.progress, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnResetSimulasi.setOnClickListener {
            currentAmountSteps = 0
            currentTenor = 0
            binding.seekJumlahPinjaman.progress = 0
            binding.seekTenor.progress = 0
            binding.btnResetSimulasi.visibility = View.GONE

            updateSimulasiText(formatter, stepAmount, minTenor, 0, 0)
            loanViewModel.simulateLoan(LoanSimulationRequest(0.toBigDecimal(), minTenor))
        }

        binding.seekJumlahPinjaman.setOnTouchListener { _, _ ->
            binding.btnResetSimulasi.visibility = View.VISIBLE
            false
        }
        binding.seekTenor.setOnTouchListener { _, _ ->
            binding.btnResetSimulasi.visibility = View.VISIBLE
            false
        }

        // Observe result
        loanViewModel.previewResult.observe(viewLifecycleOwner) {
            binding.tvCicilan.text = "Estimasi Cicilan: ${formatter.format(it.estimatedInstallment.toInt())} / bulan"
            binding.tvTotalTagihan.text = "Total Tagihan: ${formatter.format(it.totalRepayment.toInt())}"
            binding.tvJumlahPencairan.text = "Jumlah Pencairan: ${formatter.format(it.disbursedAmount.toInt())}"
            binding.tvBunga.text = "Bunga: ${it.interestRate.multiply(100.toBigDecimal())}%"
            binding.tvBiayaAdmin.text = "Biaya Admin: ${formatter.format(it.feesAmount.toInt())}"
        }

        loanViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            toggleShimmerText(binding.shimmerCicilan, binding.tvCicilan, isLoading)
            toggleShimmerText(binding.shimmerTotalTagihan, binding.tvTotalTagihan, isLoading)
            toggleShimmerText(binding.shimmerJumlahPencairan, binding.tvJumlahPencairan, isLoading)
            toggleShimmerText(binding.shimmerBunga, binding.tvBunga, isLoading)
            toggleShimmerText(binding.shimmerBiayaAdmin, binding.tvBiayaAdmin, isLoading)
        }

        loanViewModel.error.observe(viewLifecycleOwner) {
            Log.e("Simulasi", it.toString())
        }
    }

    private fun updateSimulasiText(formatter: NumberFormat, stepAmount: Int, minTenor: Int, amountSteps: Int, tenorProgress: Int) {
        val amount = amountSteps * stepAmount
        val tenor = minTenor + tenorProgress
        binding.tvJumlahPinjaman.text = "Jumlah Pinjaman: ${formatter.format(amount)}"
        binding.tvTenor.text = "Tenor: $tenor Bulan"
    }

    private fun toggleShimmerText(shimmer: ShimmerFrameLayout, textView: TextView, isLoading: Boolean) {
        if (isLoading) {
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            textView.visibility = View.GONE
        } else {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
