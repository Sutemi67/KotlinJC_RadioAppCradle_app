package apc.appcradle.radioappcradle.presentation.ui.second_screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.PlaybackCurrentStatus
import apc.appcradle.radioappcradle.domain.PlayerUiState
import apc.appcradle.radioappcradle.domain.Track
import apc.appcradle.radioappcradle.presentation.ui.theme.RadioAppCradleTheme
import apc.appcradle.radioappcradle.presentation.ui.theme.Typography
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScreenLocalPlayer(
    state: State<PlayerUiState>,
    onLaunch: suspend () -> Unit,
    playLocalFile: (String, Int) -> Unit,
    playNext: () -> Unit,
    playPrevious: () -> Unit,
    playTracklist: (List<Track>) -> Unit
) {
    val animationDuration = 1000
    val context = LocalContext.current
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    var currentTrack: Track? by remember { mutableStateOf(null) }

    var localTracks by remember { mutableStateOf(getLocalMusicFiles(context)) }
    var filteredList by remember { mutableStateOf(localTracks) }
    var isLoading by remember { mutableStateOf(true) }
    var boxSize by remember { mutableStateOf(0.dp) }
    var isFilterActive by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var searchButtonColor by remember { mutableStateOf(inactiveColor) }
    var refreshButtonColor by remember { mutableStateOf(inactiveColor) }
    var albumArtUri: Uri? by remember { mutableStateOf(null) }

    val animateSearchColor = animateColorAsState(
        targetValue = searchButtonColor,
        animationSpec = tween(animationDuration)
    )
    val animateRefreshColor = animateColorAsState(
        targetValue = refreshButtonColor,
        animationSpec = tween(animationDuration)
    )
    val animatedBoxSize =
        animateDpAsState(
            targetValue = boxSize,
            animationSpec = tween(durationMillis = animationDuration)
        )
    LaunchedEffect(currentTrack) {
        albumArtUri =
            "content://media/external/audio/albumart".toUri().buildUpon()
                .appendPath(currentTrack?.albumId.toString())
                .build()
    }
    LaunchedEffect(filterText) {
        filteredList = localTracks.filter {
            it.name.trim().lowercase().contains(filterText, ignoreCase = true)
        }
    }
    LaunchedEffect(isFilterActive, isLoading) {
        searchButtonColor = if (isFilterActive) activeColor else inactiveColor
        refreshButtonColor = if (isLoading) activeColor else inactiveColor
        if (!isFilterActive) filterText = ""
    }

    LaunchedEffect(Unit) {
        onLaunch()
        isLoading = false
    }

    LaunchedEffect(state.value.playbackStatus) {
        boxSize = when (state.value.playbackStatus) {
            PlaybackCurrentStatus.PlayingSolo -> 200.dp
            else -> 0.dp
        }
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val storagePermissionState = rememberPermissionState(permission = permission)

    Scaffold(
        bottomBar = {
            if (localTracks.isNotEmpty())
                PlaybackControls(
                    uiState = state,
                    playNext = playNext,
                    playPrevious = playPrevious,
                    playTracklist = { playTracklist(localTracks) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Local Music: ${localTracks.size} tracks",
                        style = Typography.h2
                    )
                    IconButton(
                        onClick = {
                            if (storagePermissionState.status.isGranted) {
                                localTracks = getLocalMusicFiles(context)
                            } else {
                                storagePermissionState.launchPermissionRequest()
                            }
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.refresh),
                            contentDescription = "Refresh files",
                            tint = animateRefreshColor.value
                        )
                    }
                    IconButton(
                        onClick = { isFilterActive = !isFilterActive }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_search_24),
                            contentDescription = "search",
                            tint = animateSearchColor.value
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isFilterActive,
                    enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)) + expandVertically(
                        animationSpec = tween(durationMillis = animationDuration)
                    ),
                    exit = fadeOut(animationSpec = tween(durationMillis = animationDuration)) + shrinkVertically(
                        animationSpec = tween(durationMillis = animationDuration)
                    ),
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        value = filterText,
                        onValueChange = { filterText = it }
                    )
                }
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
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(animatedBoxSize.value)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            model = albumArtUri,
                            contentDescription = "Album cover for ${currentTrack?.name}",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.play_arrow),
                            error = painterResource(R.drawable.play_arrow),
                            fallback = painterResource(R.drawable.play_arrow)
                        )
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList.size) { index ->
                            TrackItem(
                                track = filteredList[index],
                                onClick = {
                                    playLocalFile(filteredList[index].data, index)
                                    currentTrack = filteredList[index]
                                },
                                state = state.value.playingTrackIndex == index
                            )
                        }
                    }
                }
            }
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
        MediaStore.Audio.Media.ALBUM_ID,
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
        val albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val data = cursor.getString(dataColumn)
            val duration = cursor.getInt(duration)
            val album = cursor.getLong(albumId)
            trackList.add(Track(id, name, data, duration, album))
        }
    }
    return trackList.sortedWith(compareBy { it.name })
}

@Preview
@Composable
private fun Preview() {
    RadioAppCradleTheme {
        val mockUiState = remember { mutableStateOf(PlayerUiState()) }
        ScreenLocalPlayer(
            state = mockUiState,
            onLaunch = { },
            playLocalFile = { _, _ -> },
            playPrevious = {},
            playNext = {},
            playTracklist = {}
        )
    }
}