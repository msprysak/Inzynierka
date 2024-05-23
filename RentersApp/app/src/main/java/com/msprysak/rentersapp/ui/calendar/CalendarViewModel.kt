package com.msprysak.rentersapp.ui.calendar

import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.repositories.JoinRequestRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import java.time.LocalDate

class CalendarViewModel: ViewModel() {

    private val repository = UserRepositoryInstance.getInstance()
    private val userData = repository.user
    private val requestRepository = JoinRequestRepository(userData)
    private val premisesRepository = PremisesRepository.getInstance(userData)

    fun getPremisesData() = premisesRepository.getPremisesData()

    fun addTask(task: String, date: LocalDate){
        premisesRepository.addTask(task, date)
    }

}