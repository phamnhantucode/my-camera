@file:OptIn(ExperimentalFoundationApi::class)

package com.phamnhantucode.mycamera.list_images.presentation.components

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R

@Composable
fun MyImageItem(
    image: ImageBitmap,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = false,
    isSelected: Boolean = false,
    onImageLongPress: () -> Unit = {},
    onImageClick: () -> Unit = {},
    index: Int = 0,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .combinedClickable(
                onLongClick = onImageLongPress,
                onClick = onImageClick
            )
            .border(
                width = 0.2.dp,
                color = MaterialTheme.colorScheme.surface,
            )
    ) {
        Image(
            bitmap = image,
            contentDescription = "Image $index",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = if (isSelected) 0.3f else 0.0f)
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .size(24.dp)
                    .border(
                        width = 1.dp,
                        color = if (isSelectable) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = CircleShape
                    ),
            ) {
                AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn(),
                    exit = scaleOut(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

}

@Preview(widthDp = 128, heightDp = 128)
@Composable
private fun MyImageItemPreview() {
    MyCameraTheme {
        MyImageItem(
            image = ResourcesCompat.getDrawable(
                Resources.getSystem(),
                R.drawable.example_image,
                null
            )!!.toBitmap().asImageBitmap(),
            isSelectable = true,
            isSelected = true,
        )
    }
}