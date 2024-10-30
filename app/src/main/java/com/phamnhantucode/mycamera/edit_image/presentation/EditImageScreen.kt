package com.phamnhantucode.mycamera.edit_image.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun EditImageScreen(
    modifier: Modifier = Modifier,
    state: EditImageState
) {
    var size by remember {
        mutableStateOf(IntSize(0, 0))
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val imageRatio = remember {
        state.image.width.toFloat() / state.image.height.toFloat()
    }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        updateTransformState(zoomChange, offsetChange, size, imageRatio, scale, offset) { scaleChange, offsetChange ->
            scale = scaleChange
            offset = offsetChange
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
    ) {
        Image(
            bitmap = state.image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = transformState)
        )
    }
}

private fun updateTransformState(
    zoomChange: Float,
    offsetChange: Offset,
    size: IntSize,
    imageRatio: Float,
    scale: Float,
    offset: Offset,
    onCalculateTransform: (Float, Offset) -> Unit
) {
    val imageHeight = (size.width / imageRatio)
    val absXOffset = size.width / 2 * (scale - 1f)
    val minYScale = size.height / imageHeight

    val newScale = (scale * zoomChange).coerceIn(1f, 10f)
    val newOffset = offset.copy(
        x = (offset.x + offsetChange.x).coerceIn(
            -absXOffset,
            absXOffset
        ),
        y = if (scale > minYScale) {
            val absYOffset = size.height / 2 * (scale / minYScale - 1f)
            (offset.y + offsetChange.y).coerceIn(
                -absYOffset,
                absYOffset
            )
        } else {
            0f
        }
    )
    onCalculateTransform(newScale, newOffset)
}