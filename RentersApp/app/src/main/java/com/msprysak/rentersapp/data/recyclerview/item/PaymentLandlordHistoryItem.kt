package com.msprysak.rentersapp.data.recyclerview.item

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.databinding.ItemPaymentLandlordHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentLandlordHistoryItem(
    private val binding: ItemPaymentLandlordHistoryBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun chooseFragment(fragment: String, paymentWithUser: PaymentWithUser) {
        if (fragment == "PaymentsHistoryFragment") {
            bind(paymentWithUser)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bind(paymentWithUser: PaymentWithUser) {
        val payment = paymentWithUser.payment
        val user = paymentWithUser.user
        binding.tenantUsername.text = user.username
        binding.paymentTitle.text = payment.paymentTitle
        binding.paymentAmount.text = payment.paymentAmount.toString() + " zł"
        if (payment.modificationDate != null) {
            binding.sentPaymentDate.text = formatDate(payment.modificationDate!!)
            binding.sentPaymentDateLabel.visibility = RecyclerView.VISIBLE
        } else {
            binding.sentPaymentDate.visibility = RecyclerView.GONE
        }
        Glide.with(binding.root)
            .load(user.profilePictureUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.tenantProfilePicture)

        binding.paymentDateSince.text = formatDate(payment.paymentSince!! )
        binding.paymentDateTo.text = formatDate(payment.paymentTo!!)

        changeStatus(payment.paymentStatus!!)
        binding.paymentPaidButton.visibility = RecyclerView.GONE


    }
    fun changeStatus(paymentStatus: String){
        when(paymentStatus){
            "paid" -> binding.paymentStatus.text = "Zapłacono"
            "pending" -> binding.paymentStatus.text = "Oczekuje"
            else -> binding.paymentStatus.text = "Nie zapłacono"
        }
    }
    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date).toString()
    }
}