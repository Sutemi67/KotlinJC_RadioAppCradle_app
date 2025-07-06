package apc.appcradle.radioappcradle.first_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(repository.stationList.size) { index ->
            AppButton(
                text = repository.stationList[index].name,
                onClick = {
                    viewModel.playStream(repository.stationList[index].url)
                    numberOfStation = index
                },
                playingStatus = playerState,
                isClicked = numberOfStation == index
            )
        }
    }
}