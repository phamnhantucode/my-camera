package com.phamnhantucode.mycamera.camera.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize

@Composable
fun RectNet(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    lineWidth: Float = 1f
) {
    var size by remember { mutableStateOf(Size(0f, 0f)) }
    LaunchedEffect(key1 = size) {
        println("size: $size")
    }
    Canvas(modifier = modifier
        .onSizeChanged {
            size = it.toSize()
        }
    ) {
        drawLine(
            color = lineColor,
            start = Offset(
                0f,
                size.height / 3,
            ),
            end = Offset(
                size.width,
                size.height / 3,
            ),
            strokeWidth = lineWidth
        )
        drawLine(
            color = lineColor,
            start = Offset(
                0f,
                size.height * 2 / 3,
            ),
            end = Offset(
                size.width,
                size.height * 2 / 3,
            ),
            strokeWidth = lineWidth
        )

        // Draw vertical lines
        drawLine(
            color = lineColor,
            start = Offset(
                size.width / 3,
                0f,
            ),
            end = Offset(
                size.width / 3,
                size.height,
            ),
            strokeWidth = lineWidth
        )
        drawLine(
            color = lineColor,
            start = Offset(
                size.width * 2 / 3,
                0f,
            ),
            end = Offset(
                size.width * 2 / 3,
                size.height,
            ),
            strokeWidth = lineWidth
        )
    }
}