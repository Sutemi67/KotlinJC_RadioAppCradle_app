package apc.appcradle.radioappcradle.second_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.radioappcradle.MainViewModel
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.PlayerState
import apc.appcradle.radioappcradle.domain.Track


@Composable
fun PlaybackControls(
    viewModel: MainViewModel,
    trackList: List<Track>
) {
    val playerState = viewModel.playingState.collectAsStateWithLifecycle()
    val borderColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val bgColor = MaterialTheme.colorScheme.surfaceContainer
    val iconNormalSize = 50.dp
    val iconBigSize = 130.dp
    var expanded = playerState.value == PlayerState.PlayingQueue
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(durationMillis = 1500))
            .height(if (expanded) iconBigSize else iconNormalSize)
            .border(width = 2.dp, shape = RoundedCornerShape(5.dp), color = borderColor)
            .background(color = bgColor),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 2000, easing = EaseIn)
                )
                .fillMaxHeight()
                .aspectRatio(0.6f),
            onClick = { viewModel.playPrevious() },
            enabled = expanded
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.previous_button),
                contentDescription = "Next"
            )
        }

        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 2000, easing = EaseIn)
                )
                .fillMaxHeight()
                .aspectRatio(1f),
            onClick = { viewModel.playTrackList(trackList, context) },
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = when (playerState.value) {
                    PlayerState.PlayingQueue -> painterResource(R.drawable.pause)
                    else -> painterResource(R.drawable.play)
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 2000, easing = EaseIn)
                )
                .fillMaxHeight()
                .aspectRatio(0.6f),
            onClick = { viewModel.playNext() },
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
