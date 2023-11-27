package com.msprysak.rentersapp.data.recyclerview.item

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.interfaces.PaymentClickListener
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.databinding.ItemPaymentUserHistoryBinding
import java.util.Date
import java.util.Locale

class PaymentUserHistoryItem (
    private val binding: ItemPaymentUserHistoryBinding,
    private val clickListener: PaymentClickListener
) : RecyclerView.ViewHolder(binding.root){


    @SuppressLint("SetTextI18n")
    fun bind(paymentWithUser: PaymentWithUser, fragment: String){
        binding.paymentAmount.text = formatPaymentAmount(paymentWithUser.payment.paymentAmount!!)
        binding.paymentTitle.text = paymentWithUser.payment.paymentTitle
        binding.paymentDateSince.text = formatDate(paymentWithUser.payment.paymentSince!!)
        binding.paymentDateTo.text = formatDate(paymentWithUser.payment.paymentTo!!)
        changeStatus(paymentWithUser.payment.paymentStatus!!)
        if(fragment == "PaymentsHistoryFragment"){
            binding.paymentPaidButton.visibility = RecyclerView.GONE
        } else{
            binding.paymentPaidButton.setOnClickListener{
                clickListener.onPaidButtonClick(paymentWithUser)
            }
        }
    }

    fun formatPaymentAmount(paymentAmount: Double): String{

        return String.format("%.2f zł", paymentAmount)
    }
    fun changeStatus(paymentStatus: String){
        when(paymentStatus){
            "paid" -> binding.paymentStatus.text = "Zapłacono"
            "pending" -> binding.paymentStatus.text = "Oczekuje na zatwierdzenie"
            else -> binding.paymentStatus.text = "Nie zapłacono"
        }
    }

    fun formatDate(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date).toString()
    }
}