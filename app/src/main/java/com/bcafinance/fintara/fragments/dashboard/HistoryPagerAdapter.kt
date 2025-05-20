package com.bcafinance.fintara.fragments.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcafinance.fintara.fragments.loan.ApprovedHistoryFragment
import com.bcafinance.fintara.fragments.loan.RejectedHistoryFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ApprovedHistoryFragment()
            1 -> RejectedHistoryFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
