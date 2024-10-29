package com.phamnhantucode.mycamera.di

import com.phamnhantucode.mycamera.camera.presentation.CameraViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CameraViewModel() }
}