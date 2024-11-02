package com.phamnhantucode.mycamera.camera.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.camera.presentation.components.CameraPreview
import com.phamnhantucode.mycamera.camera.presentation.components.RectNet
import com.phamnhantucode.mycamera.camera.presentation.components.TakePictureButton

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel,
    state: CameraState
) {
    val applicationContext = LocalContext.current.applicationContext
    Box(
        modifier = modifier
    ) {
        val cameraController = remember {
            LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            }
        }
        LaunchedEffect(state.cameraSelector) {
            handleCameraControllerState(cameraController, state)
            cameraController.isTapToFocusEnabled = true
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(controller = cameraController.apply {
                imageCaptureFlashMode = when (state.flashState) {
                    FlashState.ON -> ImageCapture.FLASH_MODE_ON
                    FlashState.OFF -> ImageCapture.FLASH_MODE_OFF
                    FlashState.AUTO -> ImageCapture.FLASH_MODE_AUTO
                }
            }, modifier = Modifier.fillMaxSize())
            if (state.enableCameraNet) {
                RectNet(modifier = Modifier.fillMaxSize())
            }
        }
        CameraOptions(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            viewModel = viewModel,
            state = state
        )
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                .padding(vertical = 32.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val context = LocalContext.current
            if (state.latestImageCapture != null) {
                Image(
                    bitmap = state.latestImageCapture.asImageBitmap(),
                    contentDescription = getString(context, R.string.newest_image_capture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(58.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        .clickable { viewModel.onAction(CameraAction.NavigateToListImages) }
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
            TakePictureButton(modifier = Modifier.size(78.dp),
                onClick = { takePicture(cameraController, applicationContext, viewModel) }
            )
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
                    contentDescription = getString(context, R.string.switch_camera),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun takePicture(
    cameraController: LifecycleCameraController,
    applicationContext: Context,
    viewModel: CameraViewModel
) {
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
                Log.e("CameraScreen", "Error taking picture", exception)
            }
        }
    )
}

@Composable
private fun CameraOptions(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel,
    state: CameraState,
) {
    var isShowingFlashOptions by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .padding(top = 32.dp)
    ) {
        AnimatedVisibility(
            visible = isShowingFlashOptions,
            enter = expandIn(),
            exit = shrinkOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CameraFlashMode(
                currentFlashMode = state.flashState,
                onFlashStateChange = {
                    viewModel.onAction(CameraAction.ChangeFlashState(it))
                    isShowingFlashOptions = false
                }
            )
        }
        AnimatedVisibility(
            visible = !isShowingFlashOptions,
            modifier = Modifier.align(Alignment.Center)
        ) {
            val localContext = LocalContext.current
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                IconButton(onClick = {
                    isShowingFlashOptions = true
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            when (state.flashState) {
                                FlashState.ON -> R.drawable.ic_flash_on
                                FlashState.OFF -> R.drawable.ic_flash_off
                                FlashState.AUTO -> R.drawable.ic_flash_auto
                            }
                        ),
                        contentDescription = localContext.getString(R.string.flash_mode),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
                IconButton(onClick = {
                    viewModel.onAction(CameraAction.EnableCameraNet(!state.enableCameraNet))
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_camera_net),
                        contentDescription = localContext.getString(R.string.camera_net),
                        modifier = Modifier.size(20.dp),
                        tint = if (state.enableCameraNet) {
                            Color.Yellow
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraFlashMode(
    currentFlashMode: FlashState,
    onFlashStateChange: (FlashState) -> Unit
) {
    LazyRow(
    ) {
        items(FlashState.entries.size) { index ->
            val flashState = FlashState.entries[index]
            IconButton(
                onClick = { onFlashStateChange(flashState) },
                modifier = Modifier
                    .size(58.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        when (flashState) {
                            FlashState.ON -> R.drawable.ic_flash_on
                            FlashState.OFF -> R.drawable.ic_flash_off
                            FlashState.AUTO -> R.drawable.ic_flash_auto
                        }
                    ),
                    contentDescription = "Flash ${flashState.name}",
                    modifier = Modifier.size(20.dp),
                    tint = if (currentFlashMode == flashState) {
                        Color.Yellow
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
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
