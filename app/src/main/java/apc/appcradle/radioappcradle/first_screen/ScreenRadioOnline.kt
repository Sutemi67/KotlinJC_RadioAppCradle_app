package apc.appcradle.radioappcradle.first_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.app.components.AppButton
import apc.appcradle.radioappcradle.data.Repository
import apc.appcradle.radioappcradle.ui.theme.Typography
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenRadioOnline(
    viewModel: MainViewModel,
    repository: Repository = koinInject<Repository>()
) {
    val context = LocalContext.current
    val playerState = viewModel.uiState.collectAsState().value
    var numberOfStation by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(30.dp),
                title = {
                    if (playerState.isLoading) {
                        Column {
                            Text("Загрузка...", style = Typography.h2)
                            LinearProgressIndicator()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    playingStatus = playerState.playbackStatus,
                    isClicked = numberOfStation == index
                )
            }
        }
    }
}