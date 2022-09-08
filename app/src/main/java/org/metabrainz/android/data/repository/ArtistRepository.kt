package org.metabrainz.android.data.repository

import kotlinx.coroutines.flow.Flow
import org.metabrainz.android.data.sources.brainzplayer.Album
import org.metabrainz.android.data.sources.brainzplayer.Artist
import org.metabrainz.android.data.sources.brainzplayer.Song

interface ArtistRepository {
 fun getArtist(artistID: String) : Flow<Artist>
 fun getArtists(): Flow<List<Artist>>
 suspend fun addArtists(): Boolean
 suspend fun addAllSongsOfArtist(artist: Artist): List<Song>
 suspend fun addAllAlbumsOfArtist(artist: Artist): List<Album>
}