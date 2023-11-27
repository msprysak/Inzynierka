package com.msprysak.rentersapp.data.interfaces

import android.view.View
import com.msprysak.rentersapp.data.model.Premises

interface OnPremisesClickListener {
    fun onPremisesClick(premises: Premises, anchorView: View)
}