package apc.appcradle.radioappcradle

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import apc.appcradle.radioappcradle.data.Repository
import apc.appcradle.radioappcradle.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

const val TRACKLIST_SAVE_KEY = "track_list"

val appModule = module {
    viewModel<MainViewModel> { MainViewModel(get()) }

    single<PlaybackService> { PlaybackService() }
    single<Repository> { Repository(get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(TRACKLIST_SAVE_KEY, MODE_PRIVATE)
    }
}