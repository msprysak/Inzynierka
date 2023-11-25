package com.msprysak.rentersapp.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.databinding.FragmentPaymentsHistoryBinding

class PaymentsHistoryFragment: BaseFragment() {

    private var _binding: FragmentPaymentsHistoryBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentsHistoryBinding.inflate(inflater, container, false)

        println("PAYMENTS HISTORY FRAGMENT")
        println(paymentsViewModel.checkData())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}