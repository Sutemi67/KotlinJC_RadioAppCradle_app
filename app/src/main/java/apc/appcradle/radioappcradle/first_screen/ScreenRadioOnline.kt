package apc.appcradle.radioappcradle.first_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.app.components.AppButton
import apc.appcradle.radioappcradle.data.Repository
import org.koin.compose.koinInject

@Composable
fun ScreenRadioOnline(
    viewModel: MainViewModel,
    repository: Repository = koinInject<Repository>()
) {
    val context = LocalContext.current
    val playerState = viewModel.playingState.collectAsStateWithLifecycle().value
    var numberOfStation by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AppButton(
            text = "Rock",
            onClick = {
                viewModel.playStream(repository.stationList[0].url)
                numberOfStation = 0
            },
            playingStatus = playerState,
            isClicked = numberOfStation == 0
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(
            text = "Lo-Fi",
            onClick = {
                viewModel.playStream(repository.stationList[1].url)
                numberOfStation = 1
            },
            playingStatus = playerState,
            isClicked = numberOfStation == 1
        )
    }
}