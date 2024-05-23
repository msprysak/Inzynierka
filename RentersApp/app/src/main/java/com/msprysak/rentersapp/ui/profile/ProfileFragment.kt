package com.msprysak.rentersapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.activities.RegistrationActivity
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentProfileBinding
import com.msprysak.rentersapp.interfaces.BindUser
import java.io.ByteArrayOutputStream

class ProfileFragment : BaseFragment(), BindUser {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel>()
    private var imageBitmap: Bitmap? = null
    private lateinit var initialUsername: String
    private lateinit var initialPhoneNumber: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.saveButton.isEnabled = false
        return _binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val changePasswordButton = binding.changePasswordButton

        profileViewModel.getUserData().observe(viewLifecycleOwner) { user ->
            bindUserData(user)
        }

        changePasswordButton.setOnClickListener {
            val dialog = ChangePasswordDialogFragment()
            dialog.show(childFragmentManager, "ChangePasswordDialogFragment")
        }
        binding.saveButton.isEnabled = false
        setupTextChangeListeners()
        setupTakePictureClick()
        setupSaveButton()
        setupSignoutButton()


    }
    override fun bindUserData(user: User) {
        initialUsername = user.username!!
        initialPhoneNumber = user.phoneNumber!!

        binding.username.setText(user.username)
        binding.username.setSelection(user.username.length)
        binding.phoneNumberEditText.setText(user.phoneNumber)
        binding.emailEditText.setText(user.email)
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .circleCrop()
                .into(binding.editUserImageView)
        }

    }

    private fun setupSignoutButton() {
        binding.signoutButton.setOnClickListener {
            profileViewModel.signOut()
            val intent = Intent(requireContext(), RegistrationActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }


    private fun setupTextChangeListeners() {
        binding.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButton.isEnabled = s.toString() != initialUsername
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButton.isEnabled = s.toString() != initialPhoneNumber
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            saveProfileData()
            binding.saveButton.isEnabled = false
        }
    }

    private fun setupTakePictureClick() {
        binding.editUserImageView.setOnClickListener {
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
                        Glide.with(this)
                            .load(it)
                            .dontTransform()
                            .circleCrop()
                            .into(binding.editUserImageView)
                    }
                    binding.saveButton.isEnabled = true

                }
            }
        }


    private fun saveProfileData() {
        val username = binding.username.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()

        if (imageBitmap != null) {
            val stream = ByteArrayOutputStream()
            val result = imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if (result) {
                profileViewModel.uploadUserPhoto(byteArray)
            }
        }

        val map = mapOf(
            "username" to username,
            "phoneNumber" to phoneNumber,
        )

        profileViewModel.editProfileData(map)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}