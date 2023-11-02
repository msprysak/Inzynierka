package com.msprysak.rentersapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

object ImageHelper {

    private fun hasCameraGalleryPermissions(context: Context): Boolean {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val hasCameraPermission =
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                cameraPermission
            )
        val hasStoragePermission =
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                readStoragePermission
            )

        if (!hasCameraPermission && !hasStoragePermission) {
            return false
        }
        return true
    }

    private fun takePicture(fragment: Fragment, takePictureLauncher: ActivityResultLauncher<Intent>) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pickImageIntent = Intent(Intent.ACTION_PICK)

        val chooser = Intent.createChooser(Intent(), "Zrób zdjęcie lub wybierz z galerii")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent, pickImageIntent))
    }




}