package org.metabrainz.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.metabrainz.android.data.dao.ArtistDao
import org.metabrainz.android.data.di.brainzplayer.Transformer.toAlbumEntity
import org.metabrainz.android.data.di.brainzplayer.Transformer.toArtist
import org.metabrainz.android.data.di.brainzplayer.Transformer.toArtistEntity
import org.metabrainz.android.data.di.brainzplayer.Transformer.toSongEntity
import org.metabrainz.android.data.sources.brainzplayer.Album
import org.metabrainz.android.data.sources.brainzplayer.Artist
import org.metabrainz.android.data.sources.brainzplayer.Song
import org.metabrainz.android.presentation.features.brainzplayer.musicsource.AlbumsData
import org.metabrainz.android.presentation.features.brainzplayer.musicsource.SongData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepositoryImpl @Inject constructor(
    private val artistDao: ArtistDao
    ) : ArtistRepository {
    override fun getArtist(artistID: String): Flow<Artist> {
        val artist = artistDao.getArtistEntity(artistID)
        return artist.map {
            it.toArtist()
        }
    }

    override fun getArtists(): Flow<List<Artist>> =
        artistDao.getArtistEntities()
            .map { it ->
                it.map {
                    it.toArtist()
                }
            }

    override suspend fun addArtists(): Boolean {
        val artists = AlbumsData().fetchAlbums()
            .map {
                it.toArtistEntity()
            }
            .distinct()
        for (artist in artists) {
            artist.songs.addAll(addAllSongsOfArtist(artist.toArtist()).map {
                it.toSongEntity()
            })
            artist.albums.addAll(addAllAlbumsOfArtist(artist.toArtist()).map {
                it.toAlbumEntity()
            })
        }
        artistDao.addArtists(artists)
        return artists.isNotEmpty()
    }

    override suspend fun addAllSongsOfArtist(artist: Artist): List<Song> {
        val songData = SongData()
        val songs = songData.fetchSongs().filter {
            it.artist == artist.name
        }
            .map {
                it
            }
        return songs
    }

    override suspend fun addAllAlbumsOfArtist(artist: Artist): List<Album> {
        val albumData = AlbumsData()
        val albums = albumData.fetchAlbums().filter {
            it.artist == artist.name
        }
            .map {
                it
            }
        return albums
    }
}