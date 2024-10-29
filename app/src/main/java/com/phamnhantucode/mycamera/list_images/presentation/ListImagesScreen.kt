package com.phamnhantucode.mycamera.list_images.presentation

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.list_images.presentation.components.ActionGroupButtons
import com.phamnhantucode.mycamera.list_images.presentation.components.ListImagesAppBar
import com.phamnhantucode.mycamera.list_images.presentation.components.MyImageItem
import org.koin.androidx.compose.koinViewModel

val imageSizeRange = (96.dp..128.dp)

@Composable
fun ListImagesScreen(
    modifier: Modifier = Modifier,
    viewModel: ListImagesViewModel = koinViewModel(),
    state: ListImagesState,
) {
    Scaffold(
        topBar = {
            ListImagesAppBar(
                state = state,
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
        var minSize by remember { mutableStateOf(imageSizeRange.start) }
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        minSize = (minSize * zoom).coerceIn(imageSizeRange)
                    }
                }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = minSize),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(state.images.size, key = {it}) { index ->
                    MyImageItem(
                        image = state.images[index],
                        modifier = Modifier.fillMaxSize()
                            .animateItem(),
                        isSelectable = state.selectedImagesIndex.isNotEmpty(),
                        isSelected = state.selectedImagesIndex.contains(index),
                        onImageLongPress = {
                            viewModel.onAction(ListImagesAction.SelectImage(index))
                        },
                        onImageClick = {
                            if (state.selectedImagesIndex.isNotEmpty()) {
                                viewModel.onAction(ListImagesAction.SelectImage(index))
                            } else {
                                viewModel.onAction(ListImagesAction.OpenImage(index))
                            }
                        }
                    )
                }
            }
            AnimatedVisibility(
                visible = state.selectedImagesIndex.isNotEmpty(),
                enter = expandIn(expandFrom = Alignment.BottomCenter),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                ActionGroupButtons(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
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

