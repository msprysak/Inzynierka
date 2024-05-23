package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.interfaces.PaymentClickListener
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.data.recyclerview.item.PaymentLandlordHistoryItem
import com.msprysak.rentersapp.data.recyclerview.item.PaymentUserHistoryItem
import com.msprysak.rentersapp.databinding.ItemPaymentLandlordHistoryBinding
import com.msprysak.rentersapp.databinding.ItemPaymentUserHistoryBinding

class PaymentHistoryAdapter(private val paymentFragment: String,
                            private val isLandlord: Boolean,
    private val payments: List<PaymentWithUser>,
    private val clickListener: PaymentClickListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        return if (isLandlord) {
            val binding = ItemPaymentLandlordHistoryBinding.inflate(inflater, parent, false)
            PaymentLandlordHistoryItem(binding)
        } else {
            val binding = ItemPaymentUserHistoryBinding.inflate(inflater, parent, false)
            PaymentUserHistoryItem(binding, clickListener)
        }
    }

    override fun getItemCount(): Int {
        return payments.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = payments[position]
        if (holder is PaymentLandlordHistoryItem) {
            holder.chooseFragment(paymentFragment,item)

        } else if (holder is PaymentUserHistoryItem) {
            holder.bind(item,paymentFragment)

        }
    }
}