package com.msprysak.rentersapp.ui.home

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream


class HomeFragment : BaseFragment() {


    private val homeViewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private lateinit var initialName: String
    private lateinit var initialAddress: String
    private var imageBitmap: Bitmap? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.saveButton.isEnabled = false
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = binding.saveButton
        val editHomeImageView = binding.editHomeImageView
        val homeNameEditText = binding.homeNameEditText
        val addressEditText = binding.addressEditText

        homeViewModel.getPremisesData().observe(viewLifecycleOwner) { premises ->
            bindPremisesData(premises)
        }

        if (homeViewModel.getUserData().houseRoles!!.containsValue("tenant")) {
            saveButton.visibility = View.GONE
            editHomeImageView.isEnabled = false
            homeNameEditText.isEnabled = false
            addressEditText.isEnabled = false


        }

        setupTextChangeListeners()
        setupTakePictureClick()
        setupSaveButton()
    }

    private fun savePermisesData() {
        val name = binding.homeNameEditText.text.toString()
        val address = binding.addressEditText.text.toString()

        if (imageBitmap != null) {
            val stream = ByteArrayOutputStream()
            val result = imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if (result) {
                homeViewModel.uploadPremisesPhoto(byteArray)
            }
        }

        val map = mapOf(
            "name" to name,
            "address" to address,
        )

        homeViewModel.editPermisesData(map)
    }

    private fun setupTextChangeListeners() {
        binding.homeNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButton.isEnabled = s.toString() != initialName
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButton.isEnabled = s.toString() != initialAddress
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }
    private fun setupSaveButton(){
        binding.saveButton.setOnClickListener {
            savePermisesData()
            binding.saveButton.isEnabled = false
        }
    }
    private fun bindPremisesData(premises: Premises){
        initialName = premises.name!!
        initialAddress = premises.address!!

        binding.addressEditText.setText(premises.address)
        binding.homeNameEditText.setText(premises.name)
        if (!premises.premisesImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(premises.premisesImageUrl)
                .circleCrop()
                .into(binding.editHomeImageView)
        }
    }
    private fun setupTakePictureClick() {
        binding.editHomeImageView.setOnClickListener {
            if (hasCameraGalleryPermissions()) {
                selectImage(pictureResult) { pictureResult ->
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
                            .into(binding.editHomeImageView)
                    }
                    binding.saveButton.isEnabled = true

                }
            }
        }


}