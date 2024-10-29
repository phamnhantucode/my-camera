package com.phamnhantucode.mycamera.list_images.presentation

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.list_images.presentation.components.MyImageItem

val imageSizeRange = (96.dp .. 128.dp)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListImagesScreen(
    modifier: Modifier = Modifier,
    state: ListImagesState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Images") },
                actions = {
                }
            )
        }
    ) {innerPadding ->
        var minSize by remember { mutableStateOf(imageSizeRange.start) }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = minSize),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        minSize = (minSize * zoom).coerceIn(imageSizeRange)
                    }
                }
        ) {
            items(state.images.size) { index ->
                MyImageItem(
                    image = state.images[index],
                    modifier = Modifier.fillMaxSize(),
                    index = index,
                    isSelectable = state.selectedImagesIndex.isNotEmpty(),
                    isSelected = state.selectedImagesIndex.contains(index),
                    onImageLongPress = {

                    },
                )
            }
        }
    }

}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun ListImagesScreenPreview() {
    MyCameraTheme {
        ListImagesScreen(
            state = ListImagesState(
                images = (1..10).map {
                    BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.example_image)
                        .asImageBitmap()
                },
                isLoading = false,
            ),
        )
    }

}

