package apc.appcradle.radioappcradle.first_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FirstScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val context = LocalContext.current
    viewModel.initializeMediaController(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val url = "https://stream.deep1.ru/deep1mp3"
        Button(
            onClick = { viewModel.playStream(url) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play Stream 1")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.playStream(url) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play Stream 2")
        }
    }
}