package apc.appcradle.radioappcradle.domain

data class PlayerUiState(
    val playbackStatus: PlaybackCurrentStatus = PlaybackCurrentStatus.Stopped,
    val playingTrackIndex: Int? = null,
    val isLoading: Boolean = false,
    val streamUrl: String? = null,
    val isPlayerQueuePrepared: Boolean = false
)
