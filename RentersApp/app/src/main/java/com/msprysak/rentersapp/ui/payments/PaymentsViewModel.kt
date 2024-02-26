package com.msprysak.rentersapp.ui.payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Payment
import com.msprysak.rentersapp.data.model.PaymentWithUser
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PaymentRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import java.util.Date

class PaymentsViewModel : ViewModel() {

    private val userRepository = UserRepositoryInstance.getInstance()

    private val premisesRepository = PremisesRepository.getInstance(userRepository.getUserData())

    val userRole = userRepository.user.value!!.houseRoles!!.entries.first().value

    private val _usersListData: MutableLiveData<List<User>> = MutableLiveData()
    val usersListData: LiveData<List<User>> get() = _usersListData


    private val _selectedUsers: MutableSet<User> = HashSet()
    val selectedUsers: Set<User> get() = _selectedUsers


    private val paymentRepository = PaymentRepository()

    private val _payment = MutableLiveData<Payment>()

    private val _paymentsHistoryList: MutableLiveData<List<PaymentWithUser> > = MutableLiveData()
    val paymentsHistoryList: LiveData<List<PaymentWithUser> > get() = _paymentsHistoryList

    val payment: LiveData<Payment> get() = _payment
    init {
        _payment.value = Payment()
    }
    private val _paymentUserList: MutableLiveData<List<PaymentWithUser> > = MutableLiveData()
    val paymentUserList: LiveData<List<PaymentWithUser> > get() = _paymentUserList


    fun updatePaymentStatus(paymentId: String, status: String, callback: CallBack){
        paymentRepository.updatePaymentStatus(paymentId, status, callback)
    }
    fun clearSelectedUsers(){
        _selectedUsers.clear()
    }
    fun addSelectedUsers(usersList: MutableSet<User>){
        _selectedUsers.addAll(usersList)
    }
    fun removeSelectedUsers(user: User){
        _selectedUsers.remove(user)
    }


    fun setPaymentTitle(title: String){
        _payment.value!!.paymentTitle = title
    }
    fun setPaymentAmount(amount: Double){
        _payment.value!!.paymentAmount = amount
    }
    fun setPaymentDate(since: Date, to: Date){
        _payment.value!!.paymentSince = since
        _payment.value!!.paymentTo = to
    }



    fun createNewPayment(payment: Payment, selectedUserList: List<User>, callBack: CallBack){
        paymentRepository.setPayment(payment ,selectedUserList ,callBack)
    }


    fun getAllPayments(){
        paymentRepository.getPaymentsForLandlord{
            _paymentsHistoryList.postValue(it)

        }
    }

    fun getPaymentsForUser(){
        paymentRepository.getPaymentsForUser{
            _paymentsHistoryList.postValue(it)
        }
    }

    fun checkData(){
//        println(paymentRepository.checkData())
    }
    fun fetchUsers(){

        premisesRepository.fetchUsers{
            _usersListData.postValue(it)
        }
    }
}