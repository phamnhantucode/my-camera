package com.phamnhantucode.mycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.core.helper.PermissionHelper
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageAction
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageScreen
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.requestCameraPermissions(this, 0)
        enableEdgeToEdge()
        setContent {
            MyCameraTheme {
                val path = "/data/data/com.phamnhantucode.mycamera/files/202410290810049804f57d-2751-4e35-ad3b-884bf216e28b.jpeg"
                val viewModel = koinViewModel<EditImageViewModel>()
                LaunchedEffect(true) {
                    viewModel.onImageAction(EditImageAction.LoadImage(path))
                }
                val state by viewModel.state.collectAsStateWithLifecycle()
                EditImageScreen(state = state)
            }
        }
    }
}