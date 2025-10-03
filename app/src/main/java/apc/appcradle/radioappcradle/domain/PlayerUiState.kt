package apc.appcradle.radioappcradle.domain

data class PlayerUiState(
    val playbackStatus: PlaybackCurrentStatus = PlaybackCurrentStatus.Stopped,
    val playingTrackIndex: Int? = null,
    val isLoading: Boolean = false,
    val isPlayerQueuePrepared: Boolean = false,
    val localTrackList: List<Track> = emptyList(),
    val currentTrack: Track? = null,
    val isPermissionsGranted: Boolean = false
)
