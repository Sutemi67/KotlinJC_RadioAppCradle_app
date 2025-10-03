package apc.appcradle.radioappcradle.domain

data class PlayerUiState(
    val playbackStatus: PlaybackCurrentStatus = PlaybackCurrentStatus.Stopped,
    val playingTrackIndex: Int? = null,
    val isLoading: Boolean = true,
    val isPlayerQueuePrepared: Boolean = false,
    val localTrackList: List<Track> = emptyList()
)
