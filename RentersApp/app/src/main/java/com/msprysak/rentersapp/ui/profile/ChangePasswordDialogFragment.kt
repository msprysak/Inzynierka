package com.msprysak.rentersapp.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.databinding.DialogChangePasswordBinding

class ChangePasswordDialogFragment: DialogFragment() {

    private var _binding: DialogChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
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

        val saveButton = binding.saveButton
        saveButton.isEnabled = false
        val cancelButton = binding.cancelButton
        cancelButton.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        setupTextChangedListenerFragments()

    }

    private fun setupTextChangedListenerFragments() {
        val newPassword = binding.newPasswordEditText
        val confirmPassword = binding.repeatPasswordEditText

        newPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != newPassword.text.toString()){
                    binding.saveButton.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != newPassword.text.toString()) {
                    confirmPassword.error = "Hasła nie są takie same"
                }else if (s.toString().length < 6){
                    confirmPassword.error = "Hasło musi zawierać co najmniej 6 znaków"
                } else {
                    setupSaveButton(s.toString())
                }
            }
        })
    }

    private fun setupSaveButton(newPassword: String) {
        setupTextChangedListenerFragments()
        val saveButton = binding.saveButton
        saveButton.isEnabled = true
        saveButton.setOnClickListener {
            profileViewModel.updatePassword(newPassword)
            saveButton.isEnabled = false
            dismiss()
        }
    }
}

