package apc.appcradle.radioappcradle.presentation

import android.content.ComponentName
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import apc.appcradle.radioappcradle.PlaybackService
import apc.appcradle.radioappcradle.domain.PlaybackCurrentStatus
import apc.appcradle.radioappcradle.domain.PlayerUiState
import apc.appcradle.radioappcradle.domain.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    internal var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    fun initializeMediaController(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        val sessionToken = SessionToken(
            context, ComponentName(context, PlaybackService::class.java)
        )
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            updateUIWithMediaController(mediaController!!)
        }, MoreExecutors.directExecutor())
        getLocalMusicFiles(context)
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
        if (uiState.value.playbackStatus == PlaybackCurrentStatus.PlayingSolo && uiState.value.playingTrackIndex == index) {
            mediaController?.pause()
            _uiState.update {
                it.copy(
                    playbackStatus = PlaybackCurrentStatus.PausedSolo,
                    playingTrackIndex = null
                )
            }
        } else {
            if (uiState.value.playingTrackIndex == index) {
                mediaController?.play()
                _uiState.update { it.copy(playbackStatus = PlaybackCurrentStatus.PlayingSolo) }
                return
            }
            val uri = "file://$filePath"
            val mediaItem =
                MediaItem.Builder().setMediaId(uri).setMimeType(MimeTypes.AUDIO_MPEG).build()
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.play()
            _uiState.update {
                it.copy(
                    playbackStatus = PlaybackCurrentStatus.PlayingSolo,
                    playingTrackIndex = index,
                    isPlayerQueuePrepared = false
                )
            }
        }
    }

    fun playTrackList(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        if (uiState.value.playbackStatus == PlaybackCurrentStatus.PlayingQueue) {
            mediaController?.pause()
            _uiState.update { it.copy(playbackStatus = PlaybackCurrentStatus.PausedQueue) }
        } else {
            if (uiState.value.isPlayerQueuePrepared) {
                mediaController?.play()
                _uiState.update {
                    it.copy(
                        playbackStatus = PlaybackCurrentStatus.PlayingQueue,
                        playingTrackIndex = null
                    )
                }
                return
            } else {
                val mediaItems = tracks.map { track ->
                    MediaItem.Builder().setMediaId("file://${track.data}")
                        .setMimeType(MimeTypes.AUDIO_MPEG).build()
                }
                mediaController?.shuffleModeEnabled = true
                mediaController?.setMediaItems(mediaItems)
                mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                mediaController?.prepare()
                mediaController?.play()
                _uiState.update {
                    it.copy(
                        playbackStatus = PlaybackCurrentStatus.PlayingQueue,
                        playingTrackIndex = null,
                        isPlayerQueuePrepared = true,
                    )
                }
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
//                        _uiState.update { it.copy(isLoading = true) }
                    }

                    Player.STATE_READY -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    Player.STATE_ENDED -> {

                    }

                    Player.STATE_IDLE -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {

            }
        })
    }

    fun getLocalMusicFiles(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        val trackList = mutableListOf<Track>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,     //EXTERNAL для флешки INTERNAL для внутренней памяти
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)
                val duration = cursor.getInt(duration)
                val album = cursor.getLong(albumId)
                trackList.add(Track(id, name, data, duration, album))
            }
        }
        val sortedList = trackList.sortedWith(compareBy { element -> element.name })
        _uiState.update {
            it.copy(
                localTrackList = sortedList,
                isLoading = false
            )
        }
    }
}