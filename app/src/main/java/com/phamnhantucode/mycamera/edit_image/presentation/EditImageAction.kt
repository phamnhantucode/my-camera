package com.phamnhantucode.mycamera.edit_image.presentation

import android.net.Uri

sealed interface EditImageAction {
    data object DeleteImage : EditImageAction
    data class LoadImage(val path: String) : EditImageAction
    data class LoadEditedImage(val uri: Uri) : EditImageAction
    data object EditImage : EditImageAction
    data object ClickedImage : EditImageAction
    data object BackToOriginal : EditImageAction
    data object SaveWithReplace : EditImageAction
    data object SaveWithNew : EditImageAction
    data class ChangeEditType(val type: EditType) : EditImageAction
}