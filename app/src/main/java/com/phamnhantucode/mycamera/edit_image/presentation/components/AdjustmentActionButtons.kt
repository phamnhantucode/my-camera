package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentAction
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentState
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentType
import org.koin.androidx.compose.koinViewModel

@Composable
fun AdjustmentActionButtons(
    state: AdjustmentState = AdjustmentState(),
    viewModel: EditImageViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val lazyListState = rememberLazyListState()
        LaunchedEffect(key1 = state.currentAdjustmentType) {
            lazyListState.scrollToItem(state.currentAdjustmentType.ordinal)
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(
                    rememberScrollState(),
                    orientation = Orientation.Horizontal,
                    enabled = true
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(AdjustmentType.entries.size) { index ->
                AdjustmentActionButton(
                    iconRes = when (AdjustmentType.entries[index]) {
                        AdjustmentType.Brightness -> R.drawable.ic_brightness
                        AdjustmentType.Contrast -> R.drawable.ic_contrast
                        AdjustmentType.Saturation -> R.drawable.ic_saturation
                        AdjustmentType.Warmth -> R.drawable.ic_temprature
                        AdjustmentType.Sharpness -> R.drawable.ic_sharpness
                        AdjustmentType.Shadow -> R.drawable.ic_shadow
                        AdjustmentType.Blur -> R.drawable.ic_blur
                    },
                    onClick = {
                        viewModel.onAdjustmentAction(
                            AdjustmentAction.ChangeAdjustmentType(
                                AdjustmentType.entries[index]
                            )
                        )
                    },
                    isSelected = state.currentAdjustmentType == AdjustmentType.entries[index],
                    isModified = state.adjustments.first { it.type == AdjustmentType.entries[index] }.value != AdjustmentType.entries[index].defaultValue
                )
            }
        }
        val context = LocalContext.current
        Text(
            text = state.currentAdjustmentType.getStringResource(context),
            color = Color.White
        )
        MyCameraRuler(
            valueRange = state.currentAdjustmentType.getIntRange(),
            stepEachLine = 1,
            stepEachHigherLine = 1,
            initValue = state.adjustments.first { it.type == state.currentAdjustmentType }.value,
            linesColor = Color.White,
            indicatorColor = Color.White,
            labelColor = Color.White,
            keyRecalculate = Pair(
                state.currentAdjustmentType,
                state.adjustments.first { it.type == state.currentAdjustmentType }.value
            ),
            formattedValue = {
                it.toString()
            },
            showCircleInitPoint = false,
            onValueChange = { value ->
                viewModel.onAdjustmentAction(
                    AdjustmentAction.ChangeAdjustmentValue(
                        value
                    )
                )
            })
    }
}

@Composable
private fun AdjustmentActionButton(
    iconRes: Int,
    contentColor: Color = Color.White,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isModified: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = if (isModified) 1f else 0f),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = contentColor.copy(alpha = if (isSelected) 1f else 0.5f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "Icon",
            modifier = Modifier.size(20.dp),
            tint = contentColor
        )
    }
}

@Preview
@Composable
private fun AdjustmentActionButtonsPreview() {
    MyCameraTheme {
        AdjustmentActionButtons()
    }
}