package apc.appcradle.radioappcradle.app

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.PlaybackService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

const val TRACKLIST_SAVE_KEY = "track_list"

val appModule = module {
    viewModel<MainViewModel> { MainViewModel(get()) }

    single<PlaybackService> { PlaybackService() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(TRACKLIST_SAVE_KEY, MODE_PRIVATE)
    }
}