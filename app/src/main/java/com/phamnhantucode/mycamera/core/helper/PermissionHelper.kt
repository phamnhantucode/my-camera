package com.phamnhantucode.mycamera.core.helper

import android.content.Context

object PermissionHelper {
    private val CAMERA_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
    )

    private fun hasCameraPermissions(context: Context): Boolean {
        return CAMERA_PERMISSIONS.all {
            context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestCameraPermissions(context: Context, requestCode: Int) {
        if (!hasCameraPermissions(context)) {
            (context as android.app.Activity).requestPermissions(CAMERA_PERMISSIONS, requestCode)
        }
    }
}