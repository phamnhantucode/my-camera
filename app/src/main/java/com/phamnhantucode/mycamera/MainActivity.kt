package com.phamnhantucode.mycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.camera.presentation.CameraScreen
import com.phamnhantucode.mycamera.camera.presentation.CameraState
import com.phamnhantucode.mycamera.core.helper.PermissionHelper
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.requestCameraPermissions(this, 0)
        setContent {
            MyCameraTheme {
                CameraScreen(
                    viewModel = koinViewModel(),
                )
            }
        }
    }
}