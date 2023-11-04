package com.msprysak.rentersapp.ui.createhome

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.activities.MainActivity
import com.msprysak.rentersapp.data.CreateHomeCallback
import com.msprysak.rentersapp.databinding.DialogAddHomeBinding

class AddHomeDialogFragment : DialogFragment() {

    private val LOG_DEBUG = "AddHomeDialogFragment"
    private var _binding: DialogAddHomeBinding? = null
    private val createHomeViewModel by viewModels<CreateHomeViewModel>()

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddHomeBinding.inflate(inflater, container, false)

        return _binding!!.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localNameEditText = binding.localNameEditText
        val localAddressEditText = binding.localAddressEditText
        val createButton = binding.createButton
        val cancelButton = binding.cancelButton

        createButton.isEnabled = false
        localNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                createButton.isEnabled = createHomeViewModel.validateLocalName(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createButton.isEnabled = createHomeViewModel.validateLocalName(s.toString())
            }
        })

        createButton.setOnClickListener{
            createButton.isEnabled = false
            createHomeViewModel.createHome("", localAddressEditText.text.toString(), localNameEditText.text.toString(), object : CreateHomeCallback {
                override fun onSuccess() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })


        }

        cancelButton.setOnClickListener{
            dismiss()
        }


    }

}