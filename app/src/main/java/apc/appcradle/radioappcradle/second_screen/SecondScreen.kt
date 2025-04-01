package apc.appcradle.radioappcradle.second_screen

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.PlayerState
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

    val playerState = viewModel.playingState.collectAsStateWithLifecycle()

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
                        Icon(Icons.Default.Refresh, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            PlaybackControls(
                viewModel = viewModel,
                trackList = localTracks
            )
        }
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
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Нажмите на значок обновления списка, чтобы найти локальные треки"
                    )
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
                            TrackItem(
                                track = localTracks[index],
                                onClick = {
                                    viewModel.playLocalFile(localTracks[index].data, index)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = track.name,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play"
            )
        }
    }
}


@Composable
fun PlaybackControls(
    viewModel: MainViewModel,
    trackList: List<Track>
) {
    val playerState = viewModel.playingState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { viewModel.playPrevious() }) {
            Icon(painterResource(R.drawable.previous_button), "Previous")
        }

        IconButton(onClick = {
            viewModel.playTrackList(trackList)
        }) {
            Icon(
                painter = when (playerState.value) {
                    PlayerState.PlayingQueue -> painterResource(R.drawable.pause)
                    else -> painterResource(R.drawable.play)
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(onClick = { viewModel.playNext() }) {
            Icon(painterResource(R.drawable.next_button), "Next")
        }
    }
}

private fun getLocalMusicFiles(context: Context): List<Track> {
    val trackList = mutableListOf<Track>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
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
        val duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val data = cursor.getString(dataColumn)
            val duration = cursor.getInt(duration)
            trackList.add(Track(id, name, data, duration))
            Log.i(
                "database", "data = $data, \nid=$id, \nname = $name,\nduration = $duration"
            )
        }
    }
    return trackList
}