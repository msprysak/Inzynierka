package com.msprysak.rentersapp.util

import android.os.CountDownTimer
import android.widget.ProgressBar

class ProgressBarTimerUtils {
    companion object {
        fun updateTimer(totalTimeInSeconds: Long) {
            val countDownTimer = object : CountDownTimer(totalTimeInSeconds * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    val max = totalTimeInSeconds / 1000
                }

                override fun onFinish() {
                }
            }

            countDownTimer.start()
        }
    }
}