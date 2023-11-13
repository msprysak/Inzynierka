package com.msprysak.rentersapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.msprysak.rentersapp.activities.CreateHomeActivity

open class BaseFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transInflater = TransitionInflater.from(requireContext())
        enterTransition = transInflater.inflateTransition(R.transition.slide_right)
        exitTransition = transInflater.inflateTransition(R.transition.fade_out)
    }
    protected fun startApp(){
        val intent = Intent(requireContext(), CreateHomeActivity::class.java).apply{
//                Flags FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK are used to clear the back stack of activities.
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    protected fun hasCameraGalleryPermissions(): Boolean {
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

    protected fun selectImage(takePicture: ActivityResultLauncher<Intent>, onImageSelected: (Bitmap) -> Unit) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mimeType = "image/*"
        val pickImageIntent = Intent(Intent.ACTION_PICK)
        pickImageIntent.type = mimeType

        val chooser = Intent.createChooser(Intent(), "Zrób zdjęcie lub wybierz z galerii")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent, pickImageIntent))
        takePicture.launch(chooser)
    }

}