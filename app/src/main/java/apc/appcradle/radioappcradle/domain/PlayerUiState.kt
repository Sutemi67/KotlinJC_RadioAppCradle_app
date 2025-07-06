package apc.appcradle.radioappcradle.domain

data class PlayerUiState(
    val playbackStatus: PlaybackCurrentStatus = PlaybackCurrentStatus.Stopped,
    val playingTrackIndex: Int? = null,
    val streamUrl: String? = null,
    val isLoading: Boolean = false,
    val isPlayerQueuePrepared: Boolean = false
)
