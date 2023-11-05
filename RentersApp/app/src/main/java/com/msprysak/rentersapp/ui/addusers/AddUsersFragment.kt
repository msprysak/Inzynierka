package com.msprysak.rentersapp.ui.addusers

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.databinding.FragmentAddUsersBinding
import com.msprysak.rentersapp.ui.menu.MenuViewModel

class AddUsersFragment : BaseFragment() {


    private var _binding: FragmentAddUsersBinding? = null
    private val addUsersViewModel by viewModels<AddUsersViewModel>()

    private var countDownTimer: CountDownTimer? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUsersBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createCodeButton = binding.createCodeButton
        val timer = binding.timer
        val codeTextView = binding.codeTextView
        createCodeButton.setOnClickListener {
            changeVisibilityOfViews(
                timer,
                codeTextView,
                createCodeButton,
            )
            addUsersViewModel.addTemporaryCode(generateRandomNumber())
            startCounting(timer)
        }
        codeTextView.setOnClickListener {
            context?.copyToClipboard(codeTextView.text)
            Toast.makeText(context, "Skopiowano do schowka", Toast.LENGTH_SHORT).show()
        }

    }

    private fun generateRandomNumber(): String{
        val randomNumber = (1000..9999).random().toString()
        binding.codeTextView.text = randomNumber
        return randomNumber
    }
    private fun startCounting(
        timer: TextView
    ){
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(60 * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(secondsUntilFinished: Long) {
                val secondsRemaining = secondsUntilFinished / 1000
                timer.text = "$secondsRemaining s"
            }

            override fun onFinish() {
                timer.text = "0 s"
            }
        }


        countDownTimer?.start()
    }
    private fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun changeVisibilityOfViews(
        chronometer: TextView,
        codeTextView: View,
        createCodeButton: View
    ) {
        // Przywróć widoczność placeholderTime i codeTextView
        chronometer.visibility = View.VISIBLE
        codeTextView.visibility = View.VISIBLE


        val params = createCodeButton.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = codeTextView.id
        createCodeButton.layoutParams = params


    }
}

