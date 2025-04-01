package apc.appcradle.radioappcradle

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import apc.appcradle.radioappcradle.domain.PlayerState
import apc.appcradle.radioappcradle.domain.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel() : ViewModel() {
    private var playingTrackIndex: Int? = null
    private var playerQueuePrepared = false

    private val _playingState = MutableStateFlow<PlayerState>(PlayerState.Stopped)
    val playingState: StateFlow<PlayerState> = _playingState.asStateFlow()

    private lateinit var controller: MediaController
    internal var mediaControllerFuture: ListenableFuture<MediaController>? = null

    fun initializeMediaController(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        mediaControllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.apply {
            addListener(Runnable {
                controller = get()
                updateUIWithMediaController(controller)
            }, MoreExecutors.directExecutor())
        }
    }

    fun playLocalFile(filePath: String, index: Int) {
        if (controller.isPlaying && playingTrackIndex == index) {
            controller.pause()
            _playingState.value = PlayerState.PausedSolo
        } else {
            if (playingTrackIndex == index) {
                controller.play()
                _playingState.value = PlayerState.PlayingSolo
                return
            }
            playingTrackIndex = index
            val uri = "file://$filePath"
            val mediaItem = MediaItem.Builder()
                .setMediaId(uri)
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .build()
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
            _playingState.value = PlayerState.PlayingSolo
        }
    }

    fun playTrackList(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        if (controller.isPlaying) {
            controller.pause()
            _playingState.value = PlayerState.PausedQueue
        } else {
            if (playerQueuePrepared) {
                controller.play()
                _playingState.value = PlayerState.PlayingQueue
                return
            }
            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setMediaId("file://${track.data}")
                    .setMimeType(MimeTypes.AUDIO_MPEG)
                    .build()
            }
            controller.setMediaItems(mediaItems)
            controller.repeatMode = Player.REPEAT_MODE_ALL
            controller.prepare()
            controller.play()
            playerQueuePrepared = true
            _playingState.value = PlayerState.PlayingQueue
        }
    }

    fun playNext() {
        if (controller.hasNextMediaItem()) {
            controller.seekToNext()
        }
    }

    fun playPrevious() {
        if (controller.hasPreviousMediaItem()) {
            controller.seekToPrevious()
        }
    }

    fun playStream(url: String) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Radio Player")
                    .build()
            ).build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    override fun onCleared() {
        super.onCleared()
        mediaControllerFuture?.cancel(true)
    }

    private fun updateUIWithMediaController(controller: MediaController) {
        controller.addListener(object : Player.Listener {

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int
            ) {
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                    }

                    Player.STATE_READY -> {
                    }

                    Player.STATE_ENDED -> {

                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {

            }

        })
    }
}