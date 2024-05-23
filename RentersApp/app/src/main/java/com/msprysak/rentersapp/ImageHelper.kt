package com.msprysak.rentersapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object ImageHelper {

    private const val CAMERA_PERMISSION_CODE = 101
    private const val GALLERY_PERMISSION_CODE = 102

    fun hasCameraGalleryPermissions(context: Context): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val galleryPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (!cameraPermission) {
            requestCameraPermission(context)
        }

        if (!galleryPermission) {
            requestGalleryPermission(context)
        }

        return cameraPermission && galleryPermission
    }

    private fun requestCameraPermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun requestGalleryPermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_CODE
        )
    }

    fun getOpenGalleryIntent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        return intent
    }
}





