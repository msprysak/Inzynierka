package com.msprysak.rentersapp.ui.createhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.CallBack
import com.msprysak.rentersapp.databinding.DialogHomeEnterCodeBinding

class EnterHomeCodeDialogFragment: DialogFragment() {
    private val LOG_DEBUG = "EnterHomeCodeDialogFragment"
    private var _binding: DialogHomeEnterCodeBinding? = null
    private val createHomeViewModel by viewModels<CreateHomeViewModel>()


    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogHomeEnterCodeBinding.inflate(inflater, container, false)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return _binding!!.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = binding.cancelButton
        val confirmButton = binding.confirmButton

        confirmButton.setOnClickListener {
            val code = binding.enterCodeEditText.text.toString()
            createHomeViewModel.sendJoinRequest(code, object: CallBack {
                override fun onSuccess() {
                    Toast.makeText(requireContext(), "Prośba o dołączenie została wysłana", Toast.LENGTH_SHORT).show()
                    dismiss()
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    dismiss()
                }

            })
        }

        cancelButton.setOnClickListener {
            dismiss()
        }


    }
}