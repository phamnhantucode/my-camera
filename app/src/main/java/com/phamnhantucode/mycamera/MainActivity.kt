package com.phamnhantucode.mycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.core.helper.PermissionHelper
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesScreen
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.requestCameraPermissions(this, 0)
        enableEdgeToEdge()
        setContent {
            MyCameraTheme {
                val viewModel =
                    koinViewModel<ListImagesViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                ListImagesScreen(
                    state = state
                )
            }
        }
    }
}