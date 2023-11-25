package com.msprysak.rentersapp.ui.payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Payment
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PaymentRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import java.sql.Timestamp

class PaymentsViewModel : ViewModel() {

    private val userRepository = UserRepositoryInstance.getInstance()

    private val premisesRepository = PremisesRepository.getInstance(userRepository.getUserData())

    val userRole = userRepository.user.value!!.houseRoles!!.entries.first().value

    val usersListData: MutableLiveData<List<User>> = MutableLiveData()

    private val _selectedUsers: MutableSet<User> = HashSet()
    val selectedUsers: Set<User> get() = _selectedUsers
    fun addSelectedUsers(usersList: MutableSet<User>){
        _selectedUsers.addAll(usersList)
    }
    fun removeSelectedUsers(user: User){
        _selectedUsers.remove(user)
    }

    private val paymentRepository = PaymentRepository()

    private var _payment = MutableLiveData<Payment>()
    val payment: LiveData<Payment> get() = _payment
    init {
        _payment.value = Payment()
    }
    fun setPaymentTitle(title: String){
        _payment.value!!.paymentTitle = title
    }
    fun setPaymentAmount(amount: Double){
        _payment.value!!.paymentAmount = amount
    }
    fun setPaymentDate(since: Timestamp, to: Timestamp){
        _payment.value!!.paymentSince = since
        _payment.value!!.paymentTo = to
    }



    fun createNewPayment(payment: Payment, selectedUserList: List<User>, callBack: CallBack){
        paymentRepository.setPayment(payment ,selectedUserList ,callBack)
    }


    fun checkData(){
        println(paymentRepository.checkData())
    }
    fun fetchUsers(){

        premisesRepository.fetchUsers{ usersList ->
            usersListData.postValue(usersList)
        }
    }
}