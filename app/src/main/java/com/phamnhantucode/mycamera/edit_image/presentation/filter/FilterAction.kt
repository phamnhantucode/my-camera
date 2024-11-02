package com.phamnhantucode.mycamera.edit_image.presentation.filter

enum class FilterType {
    Blur,
    Blend,
    BlackAndWhite,
}

sealed interface FilterAction {
    data class FilterTypeChange(val filterType: FilterType) : FilterAction
    data class FilterValueChange(val value: Int) : FilterAction
}