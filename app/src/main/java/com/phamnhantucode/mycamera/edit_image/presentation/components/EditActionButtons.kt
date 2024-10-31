package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R

@Composable
fun EditActionButtons(
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        IconButton(
            onClick = {
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_crop_rotate),
                contentDescription = "Icon crop rotate",
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_adjustment),
                contentDescription = "Icon adjustment",
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                contentDescription = "Icon filter",
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
        }
    }
}

@Preview
@Composable
private fun EditActionButtonsPrev(
) {
    MyCameraTheme {
        EditActionButtons()
    }

}