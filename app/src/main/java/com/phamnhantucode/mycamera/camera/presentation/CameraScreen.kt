package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.camera.presentation.components.CameraPreview
import com.phamnhantucode.mycamera.camera.presentation.components.TakePictureButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier,
) {
    val applicationContext = LocalContext.current.applicationContext
    Box {
        val state by viewModel.state
            .collectAsStateWithLifecycle()

        val cameraController = remember {
            LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            }
        }

        LaunchedEffect(state.cameraSelector) {
            handleCameraControllerState(cameraController, state)
        }

        CameraPreview(controller = cameraController, modifier = Modifier.fillMaxSize())


        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                .padding(vertical = 32.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (state.latestImageCapture != null) {
                Image(
                    bitmap = state.latestImageCapture!!.asImageBitmap(),
                    contentDescription = "Latest image capture",
                    modifier = Modifier
                        .size(58.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            }
            TakePictureButton(modifier = Modifier.size(78.dp)) {
                cameraController.takePicture(
                    ContextCompat.getMainExecutor(applicationContext),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            val matrix = Matrix().apply {
                                postRotate(image.imageInfo.rotationDegrees.toFloat())
                            }
                            val imageBitmap = Bitmap.createBitmap(
                                image.toBitmap(),
                                0,
                                0,
                                image.width,
                                image.height,
                                matrix,
                                false,
                            )
                            viewModel.onAction(
                                CameraAction.TakenPicture(
                                    imageBitmap,
                                    applicationContext.filesDir.absolutePath
                                )
                            )
                        }

                        override fun onError(exception: ImageCaptureException) {
                            super.onError(exception)
                        }
                    }
                )
            }
            IconButton(
                onClick = {
                    viewModel.onAction(CameraAction.SwitchCamera)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .size(58.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_rotate),
                    contentDescription = "Rotate camera",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun handleCameraControllerState(
    cameraController: LifecycleCameraController,
    state: CameraState
) {
    cameraController.cameraSelector = state.cameraSelector
}
