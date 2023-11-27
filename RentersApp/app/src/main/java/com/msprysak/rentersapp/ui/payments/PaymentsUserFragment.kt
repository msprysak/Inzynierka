package com.msprysak.rentersapp.ui.payments

import ItemsDecorator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.PaymentHistoryAdapter
import com.msprysak.rentersapp.data.interfaces.PaymentClickListener
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.databinding.FragmentPaymentsUserBinding

class PaymentsUserFragment: BaseFragment(), PaymentClickListener {

    private var _binding: FragmentPaymentsUserBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var userPaymentAdapter: PaymentHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentsUserBinding.inflate(inflater, container, false)

        paymentsViewModel.getPaymentsForUser()

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentsViewModel.paymentsHistoryList.observe(viewLifecycleOwner) { payments ->
            val unpaidPayments = payments.filter { payment ->
                payment.payment.paymentStatus == "unpaid"
            }
            setupRecyclerView(unpaidPayments)
        }
    }

    private fun setupRecyclerView(paymentList: List<PaymentWithUser>) {
        recyclerView = binding.paymentsRecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if (recyclerView.itemDecorationCount == 0) {
            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
        }
        userPaymentAdapter = PaymentHistoryAdapter(
            "PaymentUserFragment",
            false,
            paymentList,
            this
        )
        recyclerView.adapter = userPaymentAdapter
        userPaymentAdapter.notifyDataSetChanged()
    }

    override fun onPaidButtonClick(payment: PaymentWithUser) {
        paymentsViewModel.updatePaymentStatus(payment.payment.paymentId!!, "pending", object : com.msprysak.rentersapp.data.interfaces.CallBack {
            override fun onSuccess() {
//                paymentsViewModel.getPaymentsForUser()
                Toast.makeText(context, "Wysłano prośbę w celu potwierdzenia płatności", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }


        })
    }
}