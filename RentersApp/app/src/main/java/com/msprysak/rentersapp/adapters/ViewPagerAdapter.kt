package com.msprysak.rentersapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.msprysak.rentersapp.ui.payments.PaymentsHistoryFragment
import com.msprysak.rentersapp.ui.payments.PaymentsLandlordFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                //PaymentsLandlordFragment()
                PaymentsLandlordFragment()
            }
            else -> {
                //PaymentsLandlordFragment()
                PaymentsHistoryFragment()
            }
        }
    }

}