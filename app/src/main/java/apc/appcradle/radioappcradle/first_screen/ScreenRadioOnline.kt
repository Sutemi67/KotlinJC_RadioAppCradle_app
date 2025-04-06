package apc.appcradle.radioappcradle.first_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.app.components.AppButton

@Composable
fun ScreenRadioOnline(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val url = stringResource(R.string.radio_rock_url)
        val url2 = stringResource(R.string.radio_lofi_url)

        AppButton(
            text = "Rock",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = { viewModel.playStream(url) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(
            text = "Lo-Fi",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = { viewModel.playStream(url2) }
        )
    }
}