package com.msprysak.rentersapp.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.adapters.ViewPagerAdapter
import com.msprysak.rentersapp.databinding.FragmentPaymentsBinding

class PaymentsFragment: BaseFragment() {


    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager: ViewPager2 = binding.viewPager
        val adapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position){
                0 -> {
                    tab.text = "Płatności"
                }
                else -> {
                    tab.text = "Historia"
                }
            }
        }.attach()
    }
}