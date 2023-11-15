package com.msprysak.rentersapp.data.interfaces

import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.User

interface OnReportItemClickListener {

    fun onItemClick(pair: Pair<Reports,User>)
}