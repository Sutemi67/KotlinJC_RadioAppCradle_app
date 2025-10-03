package apc.appcradle.radioappcradle.presentation.ui.second_screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.PlaybackCurrentStatus
import apc.appcradle.radioappcradle.domain.PlayerUiState
import apc.appcradle.radioappcradle.presentation.ui.theme.RadioAppCradleTheme


@Composable
fun PlaybackControls(
    state: State<PlayerUiState>,
    playPrevious: () -> Unit,
    playNext: () -> Unit,
    playTracklist: () -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val bgColor = MaterialTheme.colorScheme.surfaceContainer

    val expanded by remember(state.value.playbackStatus) { mutableStateOf(state.value.playbackStatus == PlaybackCurrentStatus.PlayingQueue) }

    val animateSize =
        animateDpAsState(
            targetValue = if (expanded) 130.dp else 50.dp,
            animationSpec = tween(durationMillis = 2000)
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(animateSize.value)
            .border(width = 2.dp, shape = RoundedCornerShape(5.dp), color = borderColor)
            .background(color = bgColor),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxHeight()
                .aspectRatio(0.6f),
            onClick = playPrevious,
            enabled = expanded
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.previous_button),
                contentDescription = "Previous track"
            )
        }
        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxHeight()
                .aspectRatio(1f),
            onClick = playTracklist,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = when (state.value.playbackStatus) {
                    PlaybackCurrentStatus.PlayingQueue -> painterResource(R.drawable.pause)
                    else -> painterResource(R.drawable.play)
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxHeight()
                .aspectRatio(0.6f),
            onClick = playNext,
            enabled = expanded
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.next_button),
                contentDescription = "Next"
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RadioAppCradleTheme {
        Surface {
            val mockUiState = remember { mutableStateOf(PlayerUiState()) }
            PlaybackControls(
                state = mockUiState,
                playTracklist = {},
                playNext = {},
                playPrevious = {},
            )
        }
    }
}