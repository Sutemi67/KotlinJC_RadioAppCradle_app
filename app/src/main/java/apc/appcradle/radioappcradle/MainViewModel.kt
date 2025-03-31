package apc.appcradle.radioappcradle

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MainViewModel() : ViewModel() {

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