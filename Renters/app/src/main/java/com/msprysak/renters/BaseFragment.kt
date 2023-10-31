package com.msprysak.renters

import android.content.Intent
import androidx.fragment.app.Fragment
import com.msprysak.renters.activities.CreateHomeActivity

abstract class BaseFragment: Fragment() {

    protected fun startApp(){
        val intent = Intent(requireContext(), CreateHomeActivity::class.java).apply{
//                Flags FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK are used to clear the back stack of activities.
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

}