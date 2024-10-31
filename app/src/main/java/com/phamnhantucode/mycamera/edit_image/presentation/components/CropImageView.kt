package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView

@Composable
fun CropImageView(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            CropImageView(
                context,
            ).apply {
                setImageBitmap(
                    imageBitmap.asAndroidBitmap()
                )
                isAutoZoomEnabled = true
                scaleType = CropImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = modifier
    )
}