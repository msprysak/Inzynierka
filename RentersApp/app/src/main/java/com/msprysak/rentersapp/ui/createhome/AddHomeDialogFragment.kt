package com.msprysak.rentersapp.ui.createhome

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.activities.MainActivity
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.databinding.DialogAddHomeBinding
import java.io.ByteArrayOutputStream

class AddHomeDialogFragment : DialogFragment() {

    private val LOG_DEBUG = "AddHomeDialogFragment"
    private var _binding: DialogAddHomeBinding? = null
    private val createHomeViewModel by viewModels<CreateHomeViewModel>()
    private var imageBitmap: Bitmap? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddHomeBinding.inflate(inflater, container, false)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

        setupTakePictureClick()

        createButton.setOnClickListener{
            createButton.isEnabled = false
            createHomeViewModel.createPremises("", localAddressEditText.text.toString(), localNameEditText.text.toString(), object :
                CallBack {

                override fun onSuccess() {
                    if (imageBitmap != null) {
                        // Zapisz zdjęcie tylko, jeśli zostało wybrane
                        val stream = ByteArrayOutputStream()
                        val result = imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val byteArray = stream.toByteArray()

                        if (result) {
                            createHomeViewModel.uploadPremisesPhoto(byteArray)
                        }
                    }
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

    private fun setupTakePictureClick() {
        binding.homeImageView.setOnClickListener {


            if (createHomeViewModel.utils.hasCameraGalleryPermissions(requireActivity())) {
                 createHomeViewModel.utils.selectImage(pictureResult) { pictureResult ->
                    imageBitmap = pictureResult
                }
            }
        }
    }

    private val pictureResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri = data.data
                    imageBitmap = if (imageUri != null) {
                        MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUri
                        )
                    } else {
                        data.extras?.get("data") as Bitmap
                    }


                    imageBitmap?.let {
                        Log.d("Debug", "setupTakePictureClick: $it")
                        Glide.with(this)
                            .load(it)
                            .dontTransform()
                            .circleCrop()
                            .into(binding.homeImageView)
                    }
                }
            }
        }

}