package com.bcafinance.fintara.fragments.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bcafinance.fintara.R
import com.bcafinance.fintara.databinding.FragmentHistoryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyPagerAdapter: HistoryPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyPagerAdapter = HistoryPagerAdapter(this)
        binding.viewPagerHistory.adapter = historyPagerAdapter

        TabLayoutMediator(binding.tabLayoutHistory, binding.viewPagerHistory) { tab, position ->

            // Inflate custom tab layout
            val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
            val tabText = tabView.findViewById<TextView>(R.id.tabText)

            tabText.text = when (position) {
                0 -> "Disetujui"
                1 -> "Ditolak"
                else -> ""
            }

            tab.customView = tabView

        }.attach()

        // Beri jarak antar tab
        binding.tabLayoutHistory.post {
            val tabStrip = binding.tabLayoutHistory.getChildAt(0) as ViewGroup
            val tabCount = tabStrip.childCount
            for (i in 0 until tabCount) {
                val tabView = tabStrip.getChildAt(i) as ViewGroup

                // Set marginEnd 16dp kecuali tab terakhir
                val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
                val marginInPx = (16 * resources.displayMetrics.density).toInt()
                layoutParams.marginEnd = if (i != tabCount - 1) marginInPx else 0
                tabView.layoutParams = layoutParams
                tabView.requestLayout()
            }
        }

        // Update background dan teks warna saat tab dipilih
        binding.tabLayoutHistory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                tabView?.setBackgroundResource(R.drawable.tab_selected_background)
                val tabText = tabView?.findViewById<TextView>(R.id.tabText)
                tabText?.setTextColor(Color.WHITE)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                tabView?.setBackgroundResource(R.drawable.tab_unselected_background)
                val tabText = tabView?.findViewById<TextView>(R.id.tabText)
                tabText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Set tab aktif awal dengan background dan teks sesuai style
        val selectedTab = binding.tabLayoutHistory.getTabAt(binding.tabLayoutHistory.selectedTabPosition)
        selectedTab?.let {
            val tabView = it.customView
            tabView?.setBackgroundResource(R.drawable.tab_selected_background)
            val tabText = tabView?.findViewById<TextView>(R.id.tabText)
            tabText?.setTextColor(Color.WHITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
