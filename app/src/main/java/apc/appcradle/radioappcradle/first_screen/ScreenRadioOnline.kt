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
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.MainViewModel
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
        val url =
            "https://srv11.gpmradio.ru:8443/stream/pro/aac/64/16?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJrZXkiOiI3MzM2ZjBjMTE2ZmRmN2IzZThjMDA3NzEyOTBjNGJiYiIsIklQIjoiMjEyLjMuMTQyLjY0IiwiVUEiOiJNb3ppbGxhLzUuMCAoV2luZG93cyBOVCAxMC4wOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvMTM0LjAuMC4wIFNhZmFyaS81MzcuMzYiLCJSZWYiOiJodHRwczovLzEwMS5ydS8iLCJ1aWRfY2hhbm5lbCI6IjE2IiwidHlwZV9jaGFubmVsIjoiY2hhbm5lbCIsInR5cGVEZXZpY2UiOiJQQyIsIkJyb3dzZXIiOiJDaHJvbWUiLCJCcm93c2VyVmVyc2lvbiI6IjEzNC4wLjAuMCIsIlN5c3RlbSI6IldpbmRvd3MgMTAiLCJleHAiOjE3NDM2NjMxNzh9.HQTXS_J-F6rtTpt9KkL4PE_9MYz3wZK6L_hno4eAVSg"
        val url2 =
            "https://srv21.gpmradio.ru:8443/stream/pro/aac/64/358?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJrZXkiOiI3MzM2ZjBjMTE2ZmRmN2IzZThjMDA3NzEyOTBjNGJiYiIsIklQIjoiMjEyLjMuMTQyLjY0IiwiVUEiOiJNb3ppbGxhLzUuMCAoV2luZG93cyBOVCAxMC4wOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvMTM0LjAuMC4wIFNhZmFyaS81MzcuMzYiLCJSZWYiOiJodHRwczovLzEwMS5ydS8iLCJ1aWRfY2hhbm5lbCI6IjM1OCIsInR5cGVfY2hhbm5lbCI6ImNoYW5uZWwiLCJ0eXBlRGV2aWNlIjoiUEMiLCJCcm93c2VyIjoiQ2hyb21lIiwiQnJvd3NlclZlcnNpb24iOiIxMzQuMC4wLjAiLCJTeXN0ZW0iOiJXaW5kb3dzIDEwIiwiZXhwIjoxNzQzNjYzNTMxfQ.D_I-PXKjg2M2wNxI6wwv3YxBdQ11S7TRJPLJi26JM9A"
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