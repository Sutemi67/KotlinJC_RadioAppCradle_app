package apc.appcradle.radioappcradle.second_screen

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.domain.Track
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var localTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isSearched by remember { mutableStateOf(false) }

    val storagePermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)

    fun searchLocalMusicFiles() {
        localTracks = getLocalMusicFiles(context)
        isSearched = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Music") },
                actions = {
                    IconButton(
                        onClick = {
                            if (storagePermissionState.status.isGranted) {
                                searchLocalMusicFiles()
                            } else {
                                storagePermissionState.launchPermissionRequest()
                            }
                        }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = { PlaybackControls() }
    ) { innerPadding ->
        if (!storagePermissionState.status.isGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Разрешение на чтение файлов не предоставлено.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                    Text("Предоставить разрешение")
                }
            }
        } else {
            if (!isSearched) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нажмите на значок поиска, чтобы найти локальные треки")
                }
            } else {
                if (localTracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Локальные треки не найдены.")
                    }
                } else {
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(localTracks.size) { index ->
                            Card(
                                content = { Text(localTracks[index].name) },
                                modifier = Modifier
                                    .clickable {
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaybackControls() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {

            }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
        }
        IconButton(onClick = {

        }) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play/Pause")
        }
        IconButton(onClick = {

        }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}

private fun getLocalMusicFiles(context: Context): List<Track> {
    val trackList = mutableListOf<Track>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        null
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val data = cursor.getString(dataColumn)
            trackList.add(Track(id, name, data))
        }
    }
    return trackList
}