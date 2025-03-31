package apc.appcradle.radioappcradle.app

import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.PlaybackService
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel<MainViewModel> { MainViewModel() }

    single<PlaybackService> { PlaybackService() }
}