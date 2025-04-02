package apc.appcradle.radioappcradle

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import apc.appcradle.radioappcradle.app.TRACKLIST_SAVE_KEY
import apc.appcradle.radioappcradle.domain.PlayerState
import apc.appcradle.radioappcradle.domain.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {
    private var playerQueuePrepared = false

    private val _playingTrackIndex = MutableStateFlow<Int?>(null)
    val playingTrackIndex: StateFlow<Int?> = _playingTrackIndex.asStateFlow()

    private val _playingState = MutableStateFlow<PlayerState>(PlayerState.Stopped)
    val playingState: StateFlow<PlayerState> = _playingState.asStateFlow()

    lateinit var controller: MediaController
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
        if (playingState.value == PlayerState.PlayingSolo && playingTrackIndex.value == index) {
            controller.pause()
            _playingState.value = PlayerState.PausedSolo
            _playingTrackIndex.value = null
        } else {
            if (playingTrackIndex.value == index) {
                controller.play()
                _playingState.value = PlayerState.PlayingSolo
                return
            }
            _playingTrackIndex.value = index
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

    fun playStream(url: String) {
        when (playingState.value) {
            PlayerState.PlayingStream -> {
                controller.stop()
                _playingState.value = PlayerState.Stopped
            }

            else -> {
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
                _playingState.value = PlayerState.PlayingStream
                _playingTrackIndex.value = null
            }
        }
    }

    fun playTrackList(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        if (playingState.value == PlayerState.PlayingQueue) {
            controller.pause()
            _playingState.value = PlayerState.PausedQueue
        } else {
            if (!controller.isPlaying) {
                if (playerQueuePrepared) {
                    controller.play()
                    _playingState.value = PlayerState.PlayingQueue
                    _playingTrackIndex.value = null
                    return
                }
            }
            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setMediaId("file://${track.data}")
                    .setMimeType(MimeTypes.AUDIO_MPEG)
                    .build()
            }
            controller.shuffleModeEnabled = true
            controller.setMediaItems(mediaItems)
            controller.repeatMode = Player.REPEAT_MODE_ALL
            controller.prepare()
            controller.play()
            playerQueuePrepared = true
            _playingState.value = PlayerState.PlayingQueue
            _playingTrackIndex.value = null
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

    fun saveTrackList(trackList: List<Track>) {
        val json = Gson().toJson(trackList)
        sharedPrefs.edit { putString(TRACKLIST_SAVE_KEY, json) }
    }

    fun loadTrackList(): List<Track> {
        val itemType = object : TypeToken<List<Track>>() {}.type
        val json = sharedPrefs.getString(TRACKLIST_SAVE_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, itemType)
    }
}