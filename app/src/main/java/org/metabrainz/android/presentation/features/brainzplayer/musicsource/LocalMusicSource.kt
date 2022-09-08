package org.metabrainz.android.presentation.features.brainzplayer.musicsource

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.metabrainz.android.data.repository.SongRepository
import org.metabrainz.android.presentation.features.brainzplayer.musicsource.State.*
import org.metabrainz.android.util.BrainzPlayerExtensions.toMediaMetadataCompat
import javax.inject.Inject

class LocalMusicSource @Inject constructor(private val songRepository: SongRepository) :
    MusicSource<MediaMetadataCompat> {

    override var songs = emptyList<MediaMetadataCompat>()

    override fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaItem =
                MediaItem.fromUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    override fun asMediaItem() = songs.map { song ->
        val mediaUri = song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI) ?: "null"
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(mediaUri.toUri())
            .setTitle(song.description.title)
            .setMediaId(song.description.mediaId)
            .setSubtitle(song.description.subtitle)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

    override fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        val listOfAllSongs = songRepository.getSongsStream().first()
        songs = listOfAllSongs.map { song ->
            song.toMediaMetadataCompat
        }
        state = STATE_INITIALIZED
    }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}