package com.msprysak.rentersapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.ui.payments.PaymentsHistoryFragment
import com.msprysak.rentersapp.ui.payments.PaymentsLandlordFragment
import com.msprysak.rentersapp.ui.payments.PaymentsUserFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val userRepository = UserRepositoryInstance.getInstance()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                if (userRepository.user.value!!.houseRoles!!.entries.first().value == "landlord")
                    PaymentsLandlordFragment()
                else
                    PaymentsUserFragment()

            }
            else -> {
                PaymentsHistoryFragment()
            }
        }
    }

}