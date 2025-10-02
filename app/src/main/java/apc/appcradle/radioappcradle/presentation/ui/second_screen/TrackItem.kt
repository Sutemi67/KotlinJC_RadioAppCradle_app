package apc.appcradle.radioappcradle.presentation.ui.second_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.Track
import apc.appcradle.radioappcradle.presentation.ui.theme.Typography


@Composable
fun TrackItem(
    state: Boolean,
    track: Track,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(60.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    if (state)
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    else
                        MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = track.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = Typography.trackText,
            )
            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                painter = when (state) {
                    true -> painterResource(R.drawable.pause_nobg)
                    false -> painterResource(R.drawable.play_arrow)
                },
                contentDescription = "Play"
            )
        }
    }
}
