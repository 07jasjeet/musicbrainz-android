package org.metabrainz.android.presentation.features.brainzplayer.services

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.metabrainz.android.presentation.features.brainzplayer.services.RepeatMode.*
import org.metabrainz.android.util.BrainzPlayerExtensions.isPlaying
import org.metabrainz.android.util.Resource

class BrainzPlayerServiceConnection(
    context: Context
) {
    private val _isConnected = MutableStateFlow(Resource(Resource.Status.LOADING, false))
    val isConnected = _isConnected.asStateFlow()

    private val _playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val playbackState = _playbackState.asStateFlow()

    private val _currentlyPlayingSong = MutableStateFlow(NOTHING_PLAYING)
    val currentPlayingSong = _currentlyPlayingSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playButtonState = MutableStateFlow(Icons.Rounded.PlayArrow)
    val playButtonState = _playButtonState.asStateFlow()

    private val _shuffleState = MutableStateFlow(false)
    val shuffleState = _shuffleState.asStateFlow()

    private val _repeatModeState = MutableStateFlow(REPEAT_MODE_OFF)
    val repeatModeState = _repeatModeState.asStateFlow()

    private var previousPlaybackState: Boolean = false
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            BrainzPlayerService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    //To skip, pause, resume etc in player
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    //subscribe and unsubscribe will be called from ViewModel to subscribe and unsubscribe from a mediaID to get access of mediaItems from local
    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.value = Resource(Resource.Status.SUCCESS, true)
        }

        override fun onConnectionSuspended() {
            _isConnected.value = Resource(Resource.Status.FAILED,false)
        }

        override fun onConnectionFailed() {
            _isConnected.value = Resource(Resource.Status.FAILED, false)
        }
    }
    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.value = state ?: EMPTY_PLAYBACK_STATE
            _playButtonState.value = if (state?.isPlaying==true) Icons.Rounded.Pause
            else Icons.Rounded.PlayArrow
            if (state?.isPlaying != previousPlaybackState) _isPlaying.value = state?.isPlaying == true
            previousPlaybackState = state?.isPlaying == true
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            when(repeatMode){
                PlaybackStateCompat.REPEAT_MODE_NONE -> _repeatModeState.value = REPEAT_MODE_OFF
                PlaybackStateCompat.REPEAT_MODE_ONE -> _repeatModeState.value = REPEAT_MODE_ONE
                PlaybackStateCompat.REPEAT_MODE_ALL -> _repeatModeState.value = REPEAT_MODE_ALL
                else -> _repeatModeState.value = REPEAT_MODE_OFF
            }
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            _shuffleState.value = shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL
                    || shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.value =
                if (metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == null)
                    NOTHING_PLAYING
                else metadata
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

enum class RepeatMode{
    REPEAT_MODE_ONE,
    REPEAT_MODE_OFF,
    REPEAT_MODE_ALL,
}