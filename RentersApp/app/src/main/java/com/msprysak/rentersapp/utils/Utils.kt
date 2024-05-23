package com.msprysak.rentersapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object Utils {

    object Utils {

        fun hasCameraGalleryPermissions(context: Context): Boolean {
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

            return hasCameraPermission && hasStoragePermission
        }

        fun selectImage(takePicture: ActivityResultLauncher<Intent>, onImageSelected: (Bitmap) -> Unit) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val mimeType = "image/jpeg"
            val pickImageIntent = Intent(Intent.ACTION_PICK)
            pickImageIntent.type = mimeType

            val chooser = Intent.createChooser(Intent(), "Zrób zdjęcie lub wybierz z galerii")
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent, pickImageIntent))
            takePicture.launch(chooser)
        }

    }



}