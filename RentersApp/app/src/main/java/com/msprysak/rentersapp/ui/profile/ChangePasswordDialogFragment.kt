package com.msprysak.rentersapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.msprysak.rentersapp.databinding.DialogChangePasswordBinding

class ChangePasswordDialogFragment: DialogFragment() {

    private var _binding: DialogChangePasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangePasswordBinding.inflate(inflater, container, false)

        return _binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = binding.cancelButton
        cancelButton.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
}