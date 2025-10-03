package apc.appcradle.radioappcradle.presentation.ui.second_screen

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
    updateTrackList: () -> Unit,
    playLocalFile: (String, Int) -> Unit,
    playNext: () -> Unit,
    playPrevious: () -> Unit,
    playTracklist: () -> Unit,
) {
    val animationDuration = 1000
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)

    var currentTrack: Track? by remember { mutableStateOf(null) }
    var localTracks by remember(state.value.localTrackList) { mutableStateOf(state.value.localTrackList) }
    var isFilterActive by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }

    val normalizedQuery by remember(filterText) { derivedStateOf { filterText.trim().lowercase() } }
    val filteredList by remember(localTracks, normalizedQuery) {
        derivedStateOf {
            if (normalizedQuery.isEmpty()) localTracks
            else localTracks.filter {
                it.name.trim().lowercase().contains(normalizedQuery, ignoreCase = true)
            }
        }
    }
    val albumArtUri = "content://media/external/audio/albumart".toUri().buildUpon()
        .appendPath(currentTrack?.albumId.toString()).build()

    val animateSearchColor = animateColorAsState(
        targetValue = if (isFilterActive) activeColor else inactiveColor,
        animationSpec = tween(animationDuration)
    )
    val animateRefreshColor = animateColorAsState(
        targetValue = if (state.value.isLoading) activeColor else inactiveColor,
        animationSpec = tween(animationDuration)
    )
    val animatedBoxSize = animateDpAsState(
        targetValue = when (state.value.playbackStatus) {
            PlaybackCurrentStatus.PlayingSolo, PlaybackCurrentStatus.PlayingQueue -> 200.dp
            else -> 0.dp
        }, animationSpec = tween(durationMillis = animationDuration)
    )
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val storagePermissionState = rememberPermissionState(permission = permission)

    LaunchedEffect(storagePermissionState.status.isGranted) {
        updateTrackList()
    }

    Scaffold(
        bottomBar = {
            if (localTracks.isNotEmpty()) PlaybackControls(
                state = state,
                playNext = {
                    playNext()
                },
                playPrevious = playPrevious,
                playTracklist = playTracklist,
            )
        }) { innerPadding ->
        Crossfade(
            targetState = state.value.playbackStatus
        ) { status ->
            when (status) {
                PlaybackCurrentStatus.PlayingQueue -> {
                    Column(
                        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            model = "content://media/external/audio/albumart".toUri().buildUpon()
                                .appendPath(state.value.currentTrack?.albumId.toString()).build(),
                            contentDescription = "Album cover for ${currentTrack?.name}",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.play_arrow),
                            error = painterResource(R.drawable.play_arrow),
                            fallback = painterResource(R.drawable.play_arrow)
                        )
                        Text(
                            modifier = Modifier.padding(15.dp),
                            text = state.value.currentTrack?.name ?: "Unknown track name",
                            style = Typography.h1,
                        )
                    }
                }

                else -> {
                    if (!storagePermissionState.status.isGranted) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Разрешение на чтение файлов не предоставлено.",
                                style = Typography.labels
                            )
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
                                            updateTrackList()
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
                            Box(Modifier.height(5.dp)) {
                                if (state.value.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                                    onValueChange = { filterText = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
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
                                    contentPadding = innerPadding, modifier = Modifier.fillMaxSize()
                                ) {
                                    items(items = filteredList, key = { it.id }) { track ->
                                        val playingIndex = state.value.playingTrackIndex
                                        val isPlaying =
                                            playingIndex != null && filteredList.getOrNull(
                                                playingIndex
                                            ) == track
                                        TrackItem(
                                            track = track,
                                            onClick = {
                                                playLocalFile(
                                                    track.data,
                                                    filteredList.indexOf(track)
                                                )
                                            },
                                            state = isPlaying
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RadioAppCradleTheme {
        val mockUiState = remember { mutableStateOf(PlayerUiState()) }
        ScreenLocalPlayer(
            state = mockUiState,
            playLocalFile = { _, _ -> },
            playPrevious = {},
            playNext = {},
            updateTrackList = {},
            playTracklist = {},
        )
    }
}