package com.phamnhantucode.mycamera.edit_image.presentation.adjustment

import android.content.Context
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.core.helper.normalizeToRange

enum class AdjustmentType(val defaultValue: Int) {
    Brightness(0),
    Contrast(0),
    Saturation(0),
    Warmth(0),
    Sharpness(0),
    Shadow(0),
    Blur(0);

    fun getStringResource(context: Context): String {
        return context.getString(
            when (this) {
                Brightness -> R.string.brightness
                Contrast -> R.string.contrast
                Saturation -> R.string.saturation
                Warmth -> R.string.warmth
                Sharpness -> R.string.sharpness
                Shadow -> R.string.shadow
                Blur -> R.string.blur
            }
        )
    }

    fun getIntRange(): IntRange {
        return when (this) {
            Brightness -> -50..50
            Contrast -> -50..50
            Saturation -> -50..50
            Warmth -> -50..50
            Sharpness -> -0..100
            Shadow -> -50..50
            Blur -> 0..100
        }
    }

    private fun getFloatingNormalRange(): ClosedFloatingPointRange<Float> {
        return when (this) {
            Brightness -> -255f..255f
            Contrast -> 0.5f..1.5f
            Saturation -> 0.5f..1.5f
            Warmth -> -2f..1f
            Sharpness -> 0f..1f
            Shadow -> -1f..1f
            Blur -> 0f..25f
        }
    }

    fun toFloatValue(value: Int): Float {
        return normalizeToRange(
            value.toFloat(),
            this.getIntRange().first.toFloat(),
            this.getIntRange().last.toFloat(),
            this.getFloatingNormalRange().start,
            this.getFloatingNormalRange().endInclusive
        )
    }
}

data class AdjustmentState(
    val currentAdjustmentType: AdjustmentType = AdjustmentType.Brightness,
    val adjustments: List<AdjustmentUnit> = AdjustmentType.entries.map { AdjustmentUnit(it, it.defaultValue) }
)

data class AdjustmentUnit(
    val type: AdjustmentType,
    val value: Int
)