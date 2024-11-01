package com.phamnhantucode.mycamera.edit_image.presentation

sealed interface EditImageAction {
    data object DeleteImage : EditImageAction
    data class LoadImage(val path: String) : EditImageAction
    data object EditImage : EditImageAction
    data object ClickedImage : EditImageAction
    data object BackToOriginal : EditImageAction
    data object SaveWithReplace : EditImageAction
    data object SaveWithNew : EditImageAction
}