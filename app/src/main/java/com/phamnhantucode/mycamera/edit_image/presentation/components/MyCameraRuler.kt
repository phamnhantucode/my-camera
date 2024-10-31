package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.core.helper.normalizeToRange
import kotlin.math.abs

enum class ScrollSpeedLevel(val value: Int) {
    Fast(10),
    Normal(5),
    Slow(2),
}

@Composable
fun MyCameraRuler(
    scrollSpeed: ScrollSpeedLevel = ScrollSpeedLevel.Normal,
    lineHeight: Dp = 6.dp,
    lineSpacing: Dp = 6.dp,
    lineThickness: Dp = 1.dp,
    stepEachHigherLine: Int = 10,
    valueRange: IntRange,
    stepEachLine: Int = 1,
    initValue: Int = 0,
    formattedValue: (value: Int) -> String,
    labelFontSize: TextUnit = 16.sp,
    linesColor: Color = Color.Black,
    indicatorColor: Color = Color.Black,
    labelColor: Color = Color.Black,
    onValueChange: (Int) -> Unit,
) {
    val defaultTextStyle =
        LocalTextStyle.current.copy(
            fontSize = labelFontSize
        )

    var lineHeightPx by remember {
        mutableFloatStateOf(0f)
    }
    var lineSpacingPx by remember {
        mutableFloatStateOf(0f)
    }
    var totalRulerSize by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = lineSpacingPx) {
        totalRulerSize = lineSpacingPx * (valueRange.step(stepEachLine).count())
    }
    var currentValue by remember {
        mutableIntStateOf(initValue)
    }
    var rulerOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var canvasWidth by remember {
        mutableFloatStateOf(0f)
    }
    val centerOffsetX = canvasWidth / 2 - totalRulerSize / 2
    LaunchedEffect(key1 = canvasWidth, key2 = totalRulerSize) {
        rulerOffset = rulerOffset.copy(
            canvasWidth / 2 - (lineSpacingPx * (valueRange.first    until initValue).step(stepEachLine).count())
        )
    }

    val transformState = rememberTransformableState { _, offset, _ ->
        rulerOffset = rulerOffset.copy(
            x = (rulerOffset.x + offset.x * (scrollSpeed.value / 10f)).coerceIn(
                -(totalRulerSize - canvasWidth / 2),
                canvasWidth/2,
            )
        )
        currentValue = normalizeToRange(
            values = rulerOffset.x,
            inputMin = canvasWidth / 2,
            inputMax = -(totalRulerSize - canvasWidth / 2),
            outputMin = valueRange.first.toFloat(),
            outputMax = valueRange.last.toFloat()
        ).toInt().coerceIn(valueRange)
        onValueChange(currentValue)
    }

    val textMeasure = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .height(lineHeight * 5)
            .fillMaxWidth()
            .onSizeChanged {
                canvasWidth = it.width.toFloat()
            }
            .transformable(state = transformState)
    ) {
        lineSpacingPx = lineSpacing.toPx()
        lineHeightPx = lineHeight.toPx()
        val lineThicknessPx = lineThickness.toPx()

        val colorsLinear = listOf(
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.1f),
            Color.Black.copy(alpha = 0f),
            Color.Black.copy(alpha = 0.1f),
            Color.Black.copy(alpha = 0.3f),
        )
        drawLinearGradient(
            start = Offset(
                0f,
                size.height/2
            ),
            end = Offset(
                size.width,
                size.height/2
            ),
            colors = colorsLinear
        )

        val translateLeft = if (
            currentValue == 0 && (abs(rulerOffset.x - centerOffsetX) < rulerOffset.x / (valueRange.count() * 5))
        ) {
            centerOffsetX
        } else {
            rulerOffset.x
        }
        translate(left = translateLeft, top = 0f) {
            for (index in (0 until valueRange.step(stepEachLine).count())) {
                val lineValue = valueRange.first + index * stepEachLine

                val lineAlpha = when {
                    abs(currentValue - lineValue) / stepEachLine * lineSpacingPx < canvasWidth / 4 -> 1f
                    else -> {
                        1 - normalizeToRange(
                            abs(currentValue - lineValue) / stepEachLine * lineSpacingPx - (canvasWidth / 4),
                            inputMin = 0f,
                            inputMax = canvasWidth / 4,
                            outputMin = 0f,
                            outputMax = 0.8f
                        )

                    }
                }
                val x = index * lineSpacingPx
                val currentLineHeight = when {
                    lineValue % stepEachHigherLine == 0 -> lineHeightPx * 1.5f
                    else -> lineHeightPx
                }
                drawLine(
                    color = linesColor.copy(alpha = lineAlpha),
                    start = Offset(x, size.height),
                    end = Offset(x, size.height - currentLineHeight),
                    strokeWidth = lineThicknessPx
                )
                if (lineValue == initValue) {
                    drawCircle(
                        color = linesColor,
                        center = Offset(x, size.height - lineHeightPx * 2f),
                        radius = lineThicknessPx
                    )
                }
            }
        }

        val textLayoutResult = textMeasure.measure(
            formattedValue(currentValue),
            style = defaultTextStyle
        )
        drawText(
            textLayoutResult,
            color = labelColor,
            topLeft = Offset(
                size.width / 2 - textLayoutResult.size.width / 2,
                size.height - lineHeightPx * 2f - textLayoutResult.size.height
            )
        )
        drawLine(
            color = indicatorColor,
            start = Offset(size.width / 2, size.height),
            end = Offset(size.width / 2, size.height - lineHeightPx * 2),
            strokeWidth = lineThicknessPx,
        )

    }
}

private fun DrawScope.drawLinearGradient(
    start: Offset,
    end: Offset,
    colors: List<Color>,
    alpha: Float = 1f,
) {
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = colors,
            start = start,
            end = end,
        ),
        alpha = alpha,
    )
}

@Preview
@Composable
private fun MyCameraRulerPreview() {
    MyCameraTheme {
        MyCameraRuler(
            valueRange = -450..450,
            formattedValue = { (it).toString() },
            stepEachLine = 10,
            scrollSpeed = ScrollSpeedLevel.Normal,
            stepEachHigherLine = 10,
            lineHeight = 8.dp,
            initValue = 0,
            labelColor = Color.Yellow,
            indicatorColor = Color.Yellow,
            linesColor = Color.Black
        ) {
        }
    }
}