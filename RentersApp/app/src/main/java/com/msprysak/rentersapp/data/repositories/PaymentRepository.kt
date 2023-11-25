package com.msprysak.rentersapp.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Payment
import com.msprysak.rentersapp.data.model.User
import java.util.UUID

class PaymentRepository {

    private val DEBUG = "PremisesRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    private val currentUser = UserRepositoryInstance.getInstance().getUserData().value


    fun checkData() {
        println("checkdata")
        println(currentUser)
    }


    fun setPayment(payment: Payment, selectedUserList: List<User>, callback: CallBack) {

        val docRef = cloud.collection("payments")
            .document(currentUser!!.houseRoles!!.keys.first())

        payment.paymentId = UUID.randomUUID().toString()

        val transaction = cloud.runTransaction { transaction ->

            selectedUserList.forEach { user ->

                payment.userId = user.userId

                val userDocRef = docRef.collection(user.userId.toString())
                    .document(payment.paymentId.toString())

                transaction.set(userDocRef, payment)

            }
            null
        }
        transaction.addOnSuccessListener {
            callback.onSuccess()
        }
            .addOnFailureListener {
                callback.onFailure(it.message.toString())
            }

    }
}