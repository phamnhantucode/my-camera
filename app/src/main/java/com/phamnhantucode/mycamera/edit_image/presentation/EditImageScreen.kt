package com.phamnhantucode.mycamera.edit_image.presentation

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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.core.presentation.components.ActionGroupButtons
import com.phamnhantucode.mycamera.core.presentation.utils.sharedImages
import com.phamnhantucode.mycamera.edit_image.presentation.components.AdjustmentActionButtons
import com.phamnhantucode.mycamera.edit_image.presentation.components.CropImageView
import com.phamnhantucode.mycamera.edit_image.presentation.components.CropRotateActionButtons
import com.phamnhantucode.mycamera.edit_image.presentation.components.EditActionButtons
import com.phamnhantucode.mycamera.edit_image.presentation.components.TopActionButton
import com.phamnhantucode.mycamera.edit_image.presentation.crop.CropRotateAction
import org.koin.androidx.compose.koinViewModel
@Composable
fun EditImageScreen(
    modifier: Modifier = Modifier,
    state: EditImageState,
    viewModel: EditImageViewModel = koinViewModel()
) {
    if (state.originImage != null) {
        Scaffold{
            val innerPadding = remember {
                it
            }
            when (state.isEditing) {
                true -> {
                    Column(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopActionButton(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            onSaveWithNew = {
                                viewModel.onImageAction(EditImageAction.SaveWithNew)
                            },
                            onBack = {
                                viewModel.onImageAction(EditImageAction.BackToOriginal)
                            },
                            onSaveWithOverwrite = {
                                viewModel.onImageAction(EditImageAction.SaveWithOverwrite)
                            }

                        )
                        Box(
                            modifier = modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            content = imageViewAfterEditEffect(state, viewModel, state.originImage)
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(), content = editingActionButtons(state, viewModel)
                        )

                    }
                }

                else -> {
                    ImagePreview(state.originImage, modifier, innerPadding, viewModel, state)
                }
            }
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
private fun editingActionButtons(
    state: EditImageState,
    viewModel: EditImageViewModel
): @Composable() (ColumnScope.() -> Unit) =
    {
        when (state.editType) {
            EditType.CROP -> {
                CropRotateActionButtons(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentColor = Color.White,
                    onRotateLeft = {
                        viewModel.onCropRotateAction(CropRotateAction.RotateLeft90Degrees)
                    },
                    onRotateDegree = { degree ->
                        viewModel.onCropRotateAction(
                            CropRotateAction.Rotate(
                                degree
                            )
                        )
                    },
                    onFlip = {
                        viewModel.onCropRotateAction(CropRotateAction.Flip)
                    }
                )
            }

            EditType.ADJUSTMENT -> {
                val adjustmentState by viewModel.adjustmentState.collectAsStateWithLifecycle()
                AdjustmentActionButtons(
                    viewModel = viewModel,
                    state = adjustmentState,
                )
            }

            EditType.FILTER -> {

            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        EditActionButtons(
            modifier = Modifier
                .fillMaxWidth(),
            contentColor = Color.White
        )
    }

@Composable
private fun imageViewAfterEditEffect(
    state: EditImageState,
    viewModel: EditImageViewModel,
    originImage: ImageBitmap
): @Composable() (BoxScope.() -> Unit) =
    {
        when (state.editType) {
            EditType.CROP -> {
                val cropRotateState by viewModel.cropRotateState.collectAsStateWithLifecycle()
                CropImageView(
                    imageBitmap = state.originImageApplyEditState
                        ?: originImage,
                    state =
                    cropRotateState,
                    modifier = Modifier
                        .align(Alignment.Center),
                    viewModel = viewModel,
                    onCropImageComplete = { uri ->
                        viewModel.onImageAction(
                            EditImageAction.LoadImage(
                                uri.path ?: ""
                            )
                        )
                    }
                )
            }

            else -> {
                if (state.editedImage != null) {
                    Image(
                        bitmap = state.editedImage,
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

@Composable
private fun ImagePreview(
    originImage: ImageBitmap,
    modifier: Modifier,
    innerPadding: PaddingValues,
    viewModel: EditImageViewModel,
    state: EditImageState
) {
    var size by remember {
        mutableStateOf(IntSize(0, 0))
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val imageRatio = remember {
        originImage.width.toFloat() / originImage.height.toFloat()
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
    Box(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    viewModel.onImageAction(EditImageAction.ClickedImage)
                }
            }
            .onSizeChanged { size = it }
    ) {
        Image(
            bitmap = state.originImage!!,
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
        AnimatedVisibility(visible = state.isShowingActionButtons) {
            Row {
                IconButton(onClick = {
                    viewModel.onImageAction(EditImageAction.BackNavigate)
                }) {
                    val context = LocalContext.current
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = getString(context, R.string.back)
                    )
                }
            }
        }
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