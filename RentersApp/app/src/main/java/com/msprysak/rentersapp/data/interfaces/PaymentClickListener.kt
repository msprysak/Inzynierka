package com.msprysak.rentersapp.data.interfaces

import com.msprysak.rentersapp.data.model.PaymentWithUser

interface PaymentClickListener {
    fun onPaidButtonClick(payment: PaymentWithUser)
}