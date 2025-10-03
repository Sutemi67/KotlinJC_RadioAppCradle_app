package apc.appcradle.radioappcradle.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.radioappcradle.presentation.MainViewModel
import apc.appcradle.radioappcradle.presentation.ui.second_screen.ScreenLocalPlayer
import apc.appcradle.radioappcradle.presentation.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PlayerMainHost() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AppCradle Audio player",
                        style = Typography.h1
                    )
                },
            )
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            ScreenLocalPlayer(
                state = uiState,
                updateTrackList = { viewModel.getLocalMusicFiles(context) },
                playLocalFile = { filePath, index -> viewModel.playLocalFile(filePath, index) },
                playNext = { viewModel.playNext() },
                playPrevious = { viewModel.playPrevious() },
                playTracklist = { viewModel.playTrackList() },
            )
        }
    }
}