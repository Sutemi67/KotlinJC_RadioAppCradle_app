package apc.appcradle.radioappcradle.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.radioappcradle.domain.PlaybackCurrentStatus
import apc.appcradle.radioappcradle.presentation.ui.theme.Typography

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    playingStatus: PlaybackCurrentStatus,
    isClicked: Boolean
) {
    val bgAnimate by animateColorAsState(
        targetValue = if (playingStatus == PlaybackCurrentStatus.PlayingStream && isClicked) MaterialTheme.colorScheme.primaryContainer else ButtonDefaults.filledTonalButtonColors().containerColor,
        animationSpec = tween(durationMillis = 1500, easing = EaseOut),
    )
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 3.dp)
            .height(50.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = bgAnimate)

    ) {
        Text(text = text, style = Typography.buttonsText)
    }
}