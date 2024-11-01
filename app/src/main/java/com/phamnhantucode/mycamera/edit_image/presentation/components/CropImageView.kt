package com.phamnhantucode.mycamera.edit_image.presentation.components

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView
import com.phamnhantucode.mycamera.core.presentation.ObserveAsEvents
import com.phamnhantucode.mycamera.edit_image.presentation.CropRotateAction
import com.phamnhantucode.mycamera.edit_image.presentation.CropRotateState
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageEvent
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CropImageView(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    state: CropRotateState = CropRotateState(),
    viewModel: EditImageViewModel = koinViewModel(),
    onCropImageComplete: (Uri) -> Unit
) {
    var view by remember {
        mutableStateOf<CropImageView?>(null)
    }
    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            is EditImageEvent.CropImage -> {
                view?.croppedImageAsync(customOutputUri = event.uri)
            }
        }
    }
    AndroidView(
        factory = { context ->
            view = CropImageView(
                context
            ).apply {
                setImageBitmap(
                    imageBitmap.asAndroidBitmap()
                )
                isAutoZoomEnabled = true
                scaleType = CropImageView.ScaleType.CENTER_CROP

                state.cropRect?.let {
                    cropRect = it
                }
            }
            view!!
        },
        modifier = modifier
    ) {
        it.apply {
            rotatedDegrees = state.rotateZDegree
            if (isFlippedHorizontally != state.flipX) {
                flipImageHorizontally()
            }
            setOnCropImageCompleteListener { _, result ->
                result.uriContent?.let { uri ->
                    onCropImageComplete(uri)
                }
                result.cropRect?.let { rect ->
                    Log.d("CropImageView", "CropImageView: $rect")
                    viewModel.onCropRotateAction(
                        CropRotateAction.CropRectChange(
                            rect
                        )
                    )
                }
            }
        }
    }
}