package com.msprysak.rentersapp.ui.payments

import ItemsDecorator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.PaymentHistoryAdapter
import com.msprysak.rentersapp.interfaces.PaymentClickListener
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.databinding.FragmentPaymentsHistoryBinding

class PaymentsHistoryFragment: BaseFragment(), PaymentClickListener {

    private var _binding: FragmentPaymentsHistoryBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsHistoryBinding.inflate(inflater, container, false)
        if (isLandlord(paymentsViewModel.userRole)) {
            paymentsViewModel.getAllPayments()
        } else {
            paymentsViewModel.getPaymentsForUser()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        paymentsViewModel.paymentsHistoryList.observe(viewLifecycleOwner) { payments ->
            setupRecyclerView(payments)
        }

    }

    private fun setupRecyclerView(paymentList: List<PaymentWithUser>) {
        recyclerView = binding.paymentsHistoryRecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if (recyclerView.itemDecorationCount == 0) {
            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
        }
        paymentHistoryAdapter = PaymentHistoryAdapter(
            "PaymentsHistoryFragment",
            isLandlord(paymentsViewModel.userRole),
            paymentList,
            this
        )
        recyclerView.adapter = paymentHistoryAdapter
        paymentHistoryAdapter.notifyDataSetChanged()
    }

    private fun isLandlord(userRole: String): Boolean {
        return userRole == "landlord"
    }

    override fun onPaidButtonClick(payment: PaymentWithUser) {
//        TODO("Not yet implemented")
    }

}