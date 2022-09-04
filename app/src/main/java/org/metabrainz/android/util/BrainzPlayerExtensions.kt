package org.metabrainz.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.metabrainz.android.R
import org.metabrainz.android.data.sources.brainzplayer.Song

object BrainzPlayerExtensions {

    //MediaDescriptionCompat extensions
    inline val MediaDescriptionCompat.toSong
    get() = Song(
        mediaID = mediaId.toString(),
        title = title.toString(),
        artist = subtitle.toString(),
        albumArt = iconUri.toString(),
        uri = mediaUri.toString()
    )

    //MediaBrowserCompat extensions
    inline val MediaBrowserCompat.MediaItem.toSong
        get() = Song(
            mediaID = mediaId!!,
            title = description.title.toString(),
            artist = description.subtitle.toString(),
            albumArt = description.iconUri.toString(),
            uri = description.mediaUri.toString()
        )

    //MediaMetadataCompat extensions
    inline val MediaMetadataCompat?.toSong
        get() = this?.description?.let {
            Song(
                mediaID = it.mediaId.toString(),
                title = it.title.toString(),
                artist = it.subtitle.toString(),
                uri = it.mediaUri.toString(),
                albumArt = it.iconUri.toString()
            )
        } ?: Song()


    inline val Song.toMediaMetadataCompat: MediaMetadataCompat
        get() = this.let { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.mediaID)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.uri)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.albumArt)
                .build()
        }

    //PlaybackStateCompat extensions
    inline val PlaybackStateCompat.isPrepared
        get() = state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED

    inline val PlaybackStateCompat.isPlaying
        get() = state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PLAYING

    inline val PlaybackStateCompat.isPlayEnabled
        get() = actions and PlaybackStateCompat.ACTION_PLAY != 0L ||
                (actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) ||
                ( state == PlaybackStateCompat.STATE_PAUSED)

    inline val PlaybackStateCompat.currentPlaybackPosition: Long
        get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
            val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
            (position + (timeDelta * playbackSpeed)).toLong()
        }
        else position

    //Image Extensions
    suspend fun String.bitmap(context: Context): Bitmap = withContext(Dispatchers.IO) {
        val imageRequest = ImageRequest.Builder(context)
            .data(this@bitmap)
            .size(128)
            .scale(Scale.FILL)
            .allowHardware(false)
            .build()

        when (val result = imageRequest.context.imageLoader.execute(imageRequest)) {
            is SuccessResult -> result.drawable.toBitmap()
            is ErrorResult -> R.drawable.ic_musicbrainz_logo_no_text.bitmap(context)
        }
    }
    suspend fun Int.bitmap(context: Context): Bitmap = withContext(Dispatchers.IO) {
        val imageRequest = ImageRequest.Builder(context)
            .data(this@bitmap)
            .size(128)
            .scale(Scale.FILL)
            .allowHardware(false)
            .build()

        when (val result = imageRequest.context.imageLoader.execute(imageRequest)) {
            is SuccessResult -> result.drawable.toBitmap()
            is ErrorResult -> BitmapFactory.decodeResource(context.resources, R.drawable.ic_musicbrainz_logo_no_text)
        }
    }
}