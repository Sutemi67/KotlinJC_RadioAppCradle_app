package apc.appcradle.radioappcradle.second_screen

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.Track
import apc.appcradle.radioappcradle.ui.theme.Typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScreenLocalPlayer(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var localTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isSearched by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val playingTrackIndex = viewModel.uiState.collectAsStateWithLifecycle().value.playingTrackIndex

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
        localTracks = viewModel.loadTrackList()
        if (localTracks.isNotEmpty()) isSearched = true
        isLoading = false
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val storagePermissionState = rememberPermissionState(permission = permission)

    fun searchLocalMusicFiles() {
        localTracks = getLocalMusicFiles(context, viewModel)
        isSearched = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (localTracks.isEmpty()) {
                        Column {
                            Text("Local Music", style = Typography.h2)
                            if (isLoading) LinearProgressIndicator(Modifier.height(1.dp))
                        }
                    } else {
                        Text("Local Music: ${localTracks.size} tracks", style = Typography.h2)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (storagePermissionState.status.isGranted) {
                                searchLocalMusicFiles()
                            } else {
                                storagePermissionState.launchPermissionRequest()
                            }
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.refresh),
                            contentDescription = "Refresh files",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                Text("Разрешение на чтение файлов не предоставлено.", style = Typography.labels)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                    Text("Предоставить разрешение", style = Typography.buttonsText)
                }
            }
        } else {
            if (!isSearched) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Нажмите на значок обновления, чтобы найти локальные треки",
                        style = Typography.labels
                    )
                }
            } else {
                if (localTracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.tracks_not_found),
                            style = Typography.labels
                        )
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
                                },
                                state = playingTrackIndex == index
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getLocalMusicFiles(context: Context, viewModel: MainViewModel): List<Track> {
    val trackList = mutableListOf<Track>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,     //EXTERNAL для флешки INTERNAL для внутренней памяти
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
    viewModel.saveTrackList(trackList)
    return trackList
}
