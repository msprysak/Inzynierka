package com.msprysak.rentersapp.data.interfaces

import android.view.View

interface OnItemClickListener {
    fun onLandlordClick(item: Any, anchorView: View)

    fun onTenantClick(item: Any, anchorView: View)
}