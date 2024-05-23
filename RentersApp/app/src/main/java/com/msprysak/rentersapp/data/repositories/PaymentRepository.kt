package com.msprysak.rentersapp.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Payment
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.interfaces.CallBack
import java.util.UUID

class PaymentRepository {

    private val DEBUG = "PaymentRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    private val currentUser = UserRepositoryInstance.getInstance().getUserData().value

    private val userRepository = UserRepositoryInstance.getInstance()
    private val premisesRepository = PremisesRepository.getInstance(userRepository.user)


    fun getPaymentsForUser(callback: (List<PaymentWithUser>) -> Unit) {
        val currentUser = currentUser

        if (currentUser?.houseRoles != null) {
            val docRef = cloud.collection("payments")
                .document(currentUser.houseRoles.keys.first())
                .collection(currentUser.userId.toString())
                .orderBy("paymentTo", com.google.firebase.firestore.Query.Direction.DESCENDING)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(DEBUG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val paymentList = mutableListOf<PaymentWithUser>()

                    snapshot.documents.forEach { document ->
                        val payment = document.toObject(Payment::class.java)
                        val paymentWithUser = PaymentWithUser(payment!!, currentUser)
                        paymentList.add(paymentWithUser)
                    }
                    callback(paymentList)
                }

            }
        }
    }

    fun updatePaymentStatus(paymentId: String, paymentStatus: String, callback: CallBack) {
        val currentUser = currentUser

        if (currentUser?.houseRoles != null) {
            val docRef = cloud.collection("payments")
                .document(currentUser.houseRoles.keys.first())
                .collection(currentUser.userId.toString())
                .document(paymentId)

            cloud.runTransaction {
                it.update(docRef, "paymentStatus", paymentStatus)
                it.update(docRef, "modificationDate", FieldValue.serverTimestamp())
                null
            }.addOnSuccessListener {
                callback.onSuccess()
            }
                .addOnFailureListener { exception ->
                    Log.d(DEBUG, " ${exception.message}")
                    callback.onFailure("Ups, coś poszło nie tak. Spróbuj ponownie później.")
                }

        }
    }

    fun getPaymentsForLandlord(callback: (List<PaymentWithUser>) -> Unit) {
        val currentUser = currentUser
        val houseRoles = currentUser?.houseRoles

        if (currentUser != null && houseRoles != null) {
            val houseId = houseRoles.keys.first()
            val docRef = cloud.collection("payments").document(houseId)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(DEBUG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val userIds = snapshot.get("userIds") as? List<*>

                    if (userIds != null) {
                        val paymentsList = mutableListOf<PaymentWithUser>()

                        userIds.forEach { userId ->
                            val userDocRef = cloud.collection("users").document(userId.toString())

                            userDocRef.get().addOnSuccessListener { userSnapshot ->
                                val userData = userSnapshot.toObject(User::class.java)

                                if (userData != null) {
                                    val userPaymentsDocRef = docRef.collection(userId.toString())
                                    userPaymentsDocRef.get()
                                        .addOnSuccessListener { userPaymentsSnapshot ->
                                            for (document in userPaymentsSnapshot.documents) {
                                                val payment = document.toObject(Payment::class.java)

                                                if (payment != null) {
                                                    val paymentWithUserData =
                                                        PaymentWithUser(payment, userData)
                                                    paymentsList.add(paymentWithUserData)
                                                }
                                            }

                                            if (userId == userIds.last()) {
                                                callback(paymentsList)
                                            }
                                        }.addOnFailureListener { exception ->
                                            Log.w(
                                                DEBUG,
                                                "Error getting user payments documents: ",
                                                exception
                                            )
                                        }
                                }
                            }.addOnFailureListener { exception ->
                                Log.w(DEBUG, "Error getting user document: ", exception)
                            }
                        }
                    }
                }
            }
        }
    }


    fun setPayment(payment: Payment, selectedUserList: List<User>, callback: CallBack) {
        val docRef = cloud.collection("payments")
            .document(premisesRepository.premises.value!!.premisesId!!)

        payment.paymentId = UUID.randomUUID().toString()

        val userIds = mutableListOf<String>()

        cloud.runTransaction { transaction ->
            selectedUserList.forEach { user ->
                val userDocRef = docRef.collection(user.userId.toString())
                    .document(payment.paymentId.toString())

                payment.userId = user.userId
                payment.paymentStatus = "unpaid"

                transaction.set(userDocRef, payment)

                userIds.add(user.userId.toString())
            }

            null
        }
            .addOnSuccessListener {
                updatePaymentsUserIdList(docRef, userIds, callback)
            }
            .addOnFailureListener { exception ->
                Log.d(DEBUG, " ${exception.message}")
                callback.onFailure("Utworzenie płatności nie powiodło się, spróbuj ponownie później.")
            }
    }

    private fun updatePaymentsUserIdList(
        docRef: DocumentReference,
        userIds: List<String>,
        callback: CallBack
    ) {
        val transaction = cloud.runTransaction { transaction ->
            val existingUserIds = transaction.get(docRef).get("userIds") as? List<*>
            if (existingUserIds != null) {
                transaction.update(
                    docRef,
                    "userIds",
                    FieldValue.arrayUnion(*userIds.toTypedArray())
                )
            } else {
                transaction.set(docRef, mapOf("userIds" to userIds))
            }
            null
        }

        transaction.addOnSuccessListener {
            callback.onSuccess()
        }
            .addOnFailureListener { exception ->
                Log.d(DEBUG, " ${exception.message}")
                callback.onFailure("Utworzenie płatności nie powiodło się, spróbuj ponownie później.")
            }

    }


}