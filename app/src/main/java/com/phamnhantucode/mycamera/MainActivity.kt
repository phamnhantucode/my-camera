package com.phamnhantucode.mycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.core.helper.PermissionHelper
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageScreen
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.requestCameraPermissions(this, 0)
        enableEdgeToEdge()
        setContent {
            MyCameraTheme {
                EditImageScreen(
                    state = EditImageState(
                        image = getDrawable(R.drawable.example_image)!!.toBitmap().asImageBitmap()
                    )
                )
            }
        }
    }
}