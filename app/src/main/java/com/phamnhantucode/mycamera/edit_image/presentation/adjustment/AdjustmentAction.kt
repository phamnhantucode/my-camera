package com.phamnhantucode.mycamera.edit_image.presentation.adjustment

sealed interface AdjustmentAction {
    data class ChangeAdjustmentType(val type: AdjustmentType) : AdjustmentAction
    data class ChangeAdjustmentValue(val value: Int) : AdjustmentAction
    data object ResetAdjustment : AdjustmentAction
}