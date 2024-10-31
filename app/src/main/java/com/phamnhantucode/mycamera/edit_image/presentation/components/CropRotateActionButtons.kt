package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R

@Composable
fun CropRotateActionButtons(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    onRotate: () -> Unit = {},
    onFlip: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = Color.Gray)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(
                onClick = onRotate
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_rotate_left),
                    contentDescription = "Icon rotate",
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
            }
            IconButton(
                onClick = onFlip
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_flip),
                    contentDescription = "Icon flip",
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        MyCameraRuler(
            valueRange = -450..450,
            formattedValue = { "${it/10f}°" },
            stepEachHigherLine = 50,
            stepEachLine = 10,
            linesColor = contentColor,
            indicatorColor = contentColor,
            labelColor = contentColor,
            initValue = 0,
            lineHeight = 8.dp,
            onValueChange = {}
        )

    }
}

@Preview
@Composable
private fun CropRotateActionButtonsPreview() {
    MyCameraTheme {
        CropRotateActionButtons()
    }
}