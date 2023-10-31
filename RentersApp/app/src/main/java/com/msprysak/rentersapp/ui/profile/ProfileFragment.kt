package com.msprysak.rentersapp.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.BindUser
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : BaseFragment(), BindUser {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var imageBitmap: Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val changePasswordButton = binding.changePasswordButton
        profileViewModel.repository.getUserData()

        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            bindUserData(user)
        }

        setupTakePictureClick()

        changePasswordButton.setOnClickListener {
            val dialog = ChangePasswordDialogFragment()
            dialog.show(childFragmentManager, "ChangePasswordDialogFragment")
        }

        setupTakePictureClick()
        setupSaveButton()

    }

    override fun bindUserData(user: User) {
        binding.username.setText(user.username)
        binding.username.setSelection(user.username!!.length)
        binding.phoneNumberEditText.setText(user.phoneNumber)
        binding.emailEditText.setText(user.email)
        if (!user.profilePictureUrl?.isEmpty()!!) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .circleCrop()
                .into(binding.editUserImageView)
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            saveProfileData()
        }
    }

    private fun setupTakePictureClick() {
        binding.editUserImageView.setOnClickListener {
            if (hasCameraGalleryPermissions()) {
                takePicture()
            }
        }
    }


    private fun hasCameraGalleryPermissions(): Boolean {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val hasCameraPermission =
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                cameraPermission
            )
        val hasStoragePermission =
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                readStoragePermission
            )

        if (!hasCameraPermission && !hasStoragePermission) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(cameraPermission, readStoragePermission),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            return true
        }
        return false
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mimeType = "image/jpeg"
        val pickImageIntent = Intent(Intent.ACTION_PICK)
        pickImageIntent.type = mimeType

        val chooser = Intent.createChooser(Intent(), "Zrób zdjęcie lub wybierz z galerii")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent, pickImageIntent))
        takePicture.launch(chooser)
    }


    private val takePicture =
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
                            .circleCrop()
                            .into(binding.editUserImageView)
                    }

                }
            }
        }


    private fun saveProfileData() {
        val username = binding.username.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()

        if (imageBitmap != null) {
            // Zapisz zdjęcie tylko, jeśli zostało wybrane
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