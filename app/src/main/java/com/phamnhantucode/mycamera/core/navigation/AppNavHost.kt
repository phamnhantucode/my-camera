package com.phamnhantucode.mycamera.core.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.phamnhantucode.mycamera.MainActivity
import com.phamnhantucode.mycamera.camera.presentation.CameraScreen
import com.phamnhantucode.mycamera.camera.presentation.CameraUiEvent
import com.phamnhantucode.mycamera.camera.presentation.CameraViewModel
import com.phamnhantucode.mycamera.core.presentation.ObserveAsEvents
import com.phamnhantucode.mycamera.core.presentation.dialog.ConfirmDeleteImagesDialog
import com.phamnhantucode.mycamera.core.presentation.dialog.ConfirmSaveOverwriteImage
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageAction
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageEvent
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageScreen
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesAction
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesEvent
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesScreen
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val view = LocalView.current
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.CameraScreen
    ) {
        composable<ScreenRoutes.CameraScreen> {
            LaunchedEffect(true) {
                MainActivity.systemUIController.hideSystemUI()
            }
            val viewModel = koinViewModel<CameraViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ObserveAsEvents(events = viewModel.events) { event ->
                when (event) {
                    is CameraUiEvent.NavigateToListImages -> {
                        navController.navigate(ScreenRoutes.ListImagesScreen)
                    }
                }
            }
            CameraScreen(
                viewModel = viewModel,
                state = state
            )
        }
        composable<ScreenRoutes.ListImagesScreen> {
            val isShowingConfirmDialog = remember { mutableStateOf(false) }
            val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
            val viewModel = koinViewModel<ListImagesViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LaunchedEffect(true) {
                MainActivity.systemUIController.apply {
                    showSystemUI()
                    setSystemBarsColor(surfaceColor)
                }
                viewModel.getImages()
            }
            ObserveAsEvents(events = viewModel.events) { event ->
                when (event) {
                    ListImagesEvent.BackNavigate -> navController.popBackStack()
                    is ListImagesEvent.NavigateToEditImage -> {
                        navController.navigate(ScreenRoutes.EditImageScreen(event.path))
                    }

                    is ListImagesEvent.ConfirmDeleteImages -> {
                        isShowingConfirmDialog.value = true
                    }
                }
            }
            if (isShowingConfirmDialog.value) {
                ConfirmDeleteImagesDialog(onConfirm = {
                    viewModel.onAction(ListImagesAction.DeleteSelectedImages)
                    isShowingConfirmDialog.value = false
                },
                    onDismiss = {
                        isShowingConfirmDialog.value = false
                    }
                )
            }
            ListImagesScreen(
                viewModel = viewModel,
                state = state
            )
        }
        composable<ScreenRoutes.EditImageScreen> { backStackEntry ->
            val isShowingConfirmDeleteDialog = remember { mutableStateOf(false) }
            val isShowingSaveDialog = remember { mutableStateOf(false) }
            val path = backStackEntry.toRoute<ScreenRoutes.EditImageScreen>().path
            val viewModel = koinViewModel<EditImageViewModel>()
            LaunchedEffect(true) {
                viewModel.onImageAction(EditImageAction.LoadImage(path))
            }
            val state by viewModel.state.collectAsStateWithLifecycle()
            LaunchedEffect(key1 = state.isShowingActionButtons) {
                MainActivity.systemUIController.apply {
                    if (state.isEditing || !state.isShowingActionButtons) {
                        hideSystemUI()
                    } else {
                        showSystemUI()
                    }
                }
            }

            ObserveAsEvents(events = viewModel.events) { event ->
                when (event) {
                    EditImageEvent.BackNavigate -> navController.popBackStack()
                    EditImageEvent.ConfirmDeleteImage -> {
                        isShowingConfirmDeleteDialog.value = true
                    }
                    EditImageEvent.DeleteImage -> {
                        isShowingSaveDialog.value = true
                    }
                }
            }
            if (isShowingConfirmDeleteDialog.value) {
                ConfirmDeleteImagesDialog(onConfirm = {
                    viewModel.onImageAction(EditImageAction.DeleteImage)
                    isShowingConfirmDeleteDialog.value = false
                },
                    onDismiss = {
                        isShowingConfirmDeleteDialog.value = false
                    }
                )
            }
            if (isShowingSaveDialog.value) {
                ConfirmSaveOverwriteImage(
                    onConfirm = {
                        viewModel.onImageAction(EditImageAction.SaveWithOverwrite)
                        isShowingSaveDialog.value = false
                    },
                    onDismiss = {
                        isShowingSaveDialog.value = false
                    }
                )
            }
            EditImageScreen(
                viewModel = viewModel,
                state = state
            )
        }
    }
}