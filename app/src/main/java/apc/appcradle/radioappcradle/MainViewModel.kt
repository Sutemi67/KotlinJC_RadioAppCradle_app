package apc.appcradle.radioappcradle

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MainViewModel(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private var playerQueuePrepared = false
    private var playingStreamUrl = ""

    private val _playingTrackIndex = MutableStateFlow<Int?>(null)
    val playingTrackIndex: StateFlow<Int?> = _playingTrackIndex.asStateFlow()

    private val _playingState = MutableStateFlow(PlayerState.Stopped)
    val playingState: StateFlow<PlayerState> = _playingState.asStateFlow()

    internal var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    fun initializeMediaController(context: Context) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        mediaControllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            updateUIWithMediaController(mediaController!!)
        }, MoreExecutors.directExecutor())
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            // Обновление UI состояния
        }
    }

    internal fun releaseController() {
        mediaController?.apply {
            removeListener(playerListener)
            release()
            mediaController = null
        }
        mediaControllerFuture?.cancel(true)
        mediaControllerFuture = null
    }

    fun playLocalFile(filePath: String, index: Int) {
        if (playingState.value == PlayerState.PlayingSolo && playingTrackIndex.value == index) {
            mediaController?.pause()
            _playingState.value = PlayerState.PausedSolo
            _playingTrackIndex.value = null
        } else {
            if (playingTrackIndex.value == index) {
                mediaController?.play()
                _playingState.value = PlayerState.PlayingSolo
                return
            }
            _playingTrackIndex.value = index
            val uri = "file://$filePath"
            val mediaItem = MediaItem.Builder()
                .setMediaId(uri)
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .build()
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.play()
            _playingState.value = PlayerState.PlayingSolo
            playerQueuePrepared = false
        }
    }

    fun playStream(url: String) {
        if (playingState.value == PlayerState.PlayingStream && playingStreamUrl == url) {
            mediaController?.stop()
            _playingState.value = PlayerState.Stopped
        } else {
            _playingState.value = PlayerState.Loading
            val mediaItem = MediaItem.Builder()
                .setMediaId(url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Radio Player")
                        .build()
                ).build()
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.play()
            playerQueuePrepared = false
            playingStreamUrl = url
            _playingState.value = PlayerState.PlayingStream
            _playingTrackIndex.value = null
        }
    }


    fun playTrackList(tracks: List<Track>, context: Context) {
        if (tracks.isEmpty()) {
            Toast.makeText(context, "В плейлисте нет треков", Toast.LENGTH_SHORT).show()
            return
        }
        if (playingState.value == PlayerState.PlayingQueue) {
            mediaController?.pause()
            _playingState.value = PlayerState.PausedQueue
//            Toast.makeText(context, "Пауза очереди", Toast.LENGTH_SHORT).show()
        } else {
            if (playerQueuePrepared) {
                mediaController?.play()
                _playingState.value = PlayerState.PlayingQueue
                _playingTrackIndex.value = null
//                Toast.makeText(context, "$mediaController", Toast.LENGTH_SHORT).show()
                return
            } else {
                val mediaItems = tracks.map { track ->
                    MediaItem.Builder()
                        .setMediaId("file://${track.data}")
                        .setMimeType(MimeTypes.AUDIO_MPEG)
                        .build()
                }
                mediaController?.shuffleModeEnabled = true
                mediaController?.setMediaItems(mediaItems)
                mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                mediaController?.prepare()
                mediaController?.play()
                playerQueuePrepared = true
                _playingState.value = PlayerState.PlayingQueue
                _playingTrackIndex.value = null
//                Toast.makeText(context, "$mediaController", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun playNext() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNext()
        }
    }

    fun playPrevious() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPrevious()
        }
    }

    override fun onCleared() {
        super.onCleared()
        releaseController()
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

    suspend fun loadTrackList(): List<Track> = withContext(Dispatchers.IO) {
        delay(1000L)
        val itemType = object : TypeToken<List<Track>>() {}.type
        val json = sharedPrefs.getString(TRACKLIST_SAVE_KEY, null) ?: return@withContext emptyList()
        Gson().fromJson(json, itemType)
    }
}