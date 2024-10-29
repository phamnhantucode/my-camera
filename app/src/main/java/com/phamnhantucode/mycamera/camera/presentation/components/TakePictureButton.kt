package com.phamnhantucode.mycamera.camera.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TakePictureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var clicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (clicked) 0.8f else 1f, animationSpec = tween(200), label = "")
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(shape = CircleShape)
            .clickable {
                coroutineScope.launch {
                    clicked = true
                    onClick()
                    delay(200)
                    clicked = false
                }
            }
    ) {
        drawCircle(
            color = Color.White,
            radius = size.minDimension / 2 * scale
        )
        drawCircle(
            color = Color.White,
            radius = size.minDimension / 2,
            style = Stroke(size.minDimension / 10),
        )
    }
}