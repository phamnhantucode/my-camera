package com.phamnhantucode.mycamera.edit_image.presentation

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phamnhantucode.mycamera.core.presentation.components.ActionGroupButtons
import com.phamnhantucode.mycamera.core.presentation.utils.sharedImages
import com.phamnhantucode.mycamera.edit_image.presentation.components.CropImageView
import com.phamnhantucode.mycamera.edit_image.presentation.components.CropRotateActionButtons
import com.phamnhantucode.mycamera.edit_image.presentation.components.EditActionButtons
import com.phamnhantucode.mycamera.edit_image.presentation.components.TopActionButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditImageScreen(
    modifier: Modifier = Modifier,
    state: EditImageState,
    viewModel: EditImageViewModel = koinViewModel()
) {
    if (state.image != null) {
        var size by remember {
            mutableStateOf(IntSize(0, 0))
        }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val imageRatio = remember {
            state.image.width.toFloat() / state.image.height.toFloat()
        }

        val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
            updateTransformState(
                zoomChange,
                offsetChange,
                size,
                imageRatio,
                scale,
                offset
            ) { newScale, newOffset ->
                scale = newScale
                offset = newOffset
            }
        }

        Scaffold {
            when (state.isEditing) {
                true -> {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                            .background(Color.Black),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopActionButton(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            onSaveACopy = {
                                viewModel.onImageAction(EditImageAction.SaveWithNew)
                            },
                            onBack = {
                                viewModel.onImageAction(EditImageAction.BackToOriginal)
                            },

                            )
                        Box(
                            modifier = modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(it)
                        ) {
                            val cropRotateState by viewModel.cropRotateState.collectAsStateWithLifecycle()
                            CropImageView(
                                imageBitmap = state.image,
                                state =
                                cropRotateState,
                                modifier = Modifier
                                    .align(Alignment.Center),
                                viewModel = viewModel,
                                onCropImageComplete = {uri ->
                                    viewModel.onImageAction(EditImageAction.LoadImage(uri.path ?: ""))
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            CropRotateActionButtons(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentColor = Color.White,
                                onRotateLeft = {
                                    viewModel.onCropRotateAction(CropRotateAction.RotateLeft90Degrees)
                                },
                                onRotateDegree = {
                                    viewModel.onCropRotateAction(CropRotateAction.Rotate(it))
                                },
                                onFlip = {
                                    viewModel.onCropRotateAction(CropRotateAction.Flip)
                                }
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            EditActionButtons(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentColor = Color.White
                            )
                        }

                    }
                }

                else -> {
                    Box(
                        modifier = modifier
                            .padding(it)
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    viewModel.onImageAction(EditImageAction.ClickedImage)
                                }
                            }
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
                        AnimatedVisibility(
                            visible = state.isShowingActionButtons,
                            enter = expandIn(expandFrom = Alignment.BottomCenter),
                            exit = fadeOut(),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            val context = LocalContext.current
                            ActionGroupButtons(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                onShared = {
                                    sharedImages(context, listOf(viewModel.getImageUri(context)))
                                },
                                onDelete = {
                                    viewModel.onImageAction(EditImageAction.DeleteImage)
                                },
                                onEdit = {
                                    viewModel.onImageAction(EditImageAction.EditImage)
                                }
                            )

                        }
                    }
                }
            }
        }
    } else {
        CircularProgressIndicator()
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