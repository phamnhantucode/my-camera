package com.phamnhantucode.mycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.camera.presentation.CameraScreen
import com.phamnhantucode.mycamera.camera.presentation.CameraUiEvent
import com.phamnhantucode.mycamera.camera.presentation.CameraViewModel
import com.phamnhantucode.mycamera.core.helper.PermissionHelper
import com.phamnhantucode.mycamera.core.navigation.AppNavHost
import com.phamnhantucode.mycamera.core.navigation.ScreenRoutes
import com.phamnhantucode.mycamera.core.presentation.ObserveAsEvents
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageAction
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageEvent
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageScreen
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesEvent
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
                AppNavHost()
            }
        }
    }
}