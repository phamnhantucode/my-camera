package com.phamnhantucode.mycamera.core.helper

import android.os.Build
import androidx.activity.ComponentActivity

class SystemUIController(
    private val activity: ComponentActivity
) {
    fun hideSystemUI() {
            activity.window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
    }

    fun showSystemUI() {
            activity.window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
    }

    fun setSystemBarsColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.statusBarColor = color
            activity.window.navigationBarColor = color
        }
    }
}