package com.phamnhantucode.mycamera.camera.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.camera.presentation.CameraZoomRatio

@Composable
fun ZoomButtons(
    modifier: Modifier = Modifier,
    currentSelected: CameraZoomRatio = CameraZoomRatio.RATIO_1X,
    onZoomSelected: (CameraZoomRatio) -> Unit = {}
) {
    val context = LocalContext.current
    LazyRow(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.5f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(CameraZoomRatio.entries.size) { index ->
            val zoomRatio = CameraZoomRatio.entries[index]
                ZoomBtn(
                    text = zoomRatio.getStringRes(context),
                    isActivated = zoomRatio == currentSelected
                ) {
                    onZoomSelected(zoomRatio)
                }
        }
    }
}

@Composable
fun ZoomBtn(
    modifier: Modifier = Modifier,
    text: String,
    isActivated: Boolean = false,
    onClick: () -> Unit
) {
    AnimatedContent(targetState = isActivated, label = "") {it ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .size(32.dp)
                .clip(CircleShape)
                .background(if (it) Color.White.copy(alpha = 0.5f) else Color.Transparent)
                .clickable {
                    onClick()
                }
        ) {
            Text(
                text = text,
                fontSize = if (it) 12.sp else 10.sp,
                textAlign = TextAlign.Center,
                color = if (isActivated) Color.Black else Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ZoomButtonsPreview() {
    MyCameraTheme {
        ZoomButtons()
    }
}