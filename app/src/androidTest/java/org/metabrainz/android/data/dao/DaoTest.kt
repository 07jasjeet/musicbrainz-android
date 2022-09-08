package org.metabrainz.android.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.metabrainz.android.data.di.brainzplayer.BrainzPlayerDatabase
import org.metabrainz.android.data.sources.brainzplayer.AlbumEntity
import org.metabrainz.android.data.sources.brainzplayer.ArtistEntity
import org.metabrainz.android.data.sources.brainzplayer.PlaylistEntity
import org.metabrainz.android.data.sources.brainzplayer.SongEntity

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DaoTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var albumDao: AlbumDao
    private lateinit var artistDao: ArtistDao
    private lateinit var playlistDao: PlaylistDao
    private lateinit var songDao: SongDao
    private lateinit var brainzPlayerDatabase: BrainzPlayerDatabase

    private val albumEntities = (1..10).map { album ->
        AlbumEntity(
            album.toLong(),
            "Album$album",
            "Artist$album",
            "AlbumArt$album",
        )
    }

    private val songEntities = (11..20).map { song->
        SongEntity(
            song.toString(),
            "Song$song",
            "Artist$song",
            "Uri$song",
            "$song",
            "Album$song",
            "AlbumArt$song"
        )
    }

    private val playlistEntities = (101..110).map {
        PlaylistEntity(
            it.toLong(),
            "Title$it",
            (11..20).map { song ->
                SongEntity(
                    "mediaID$song",
                    "SongTitle$song",
                    "Artist$song",
                    "Uri$song",
                    "AlbumID$song",
                    "Album$song",
                    "AlbumArt$song"
                )
            }
        )
    }

    private val artistEntities = (111..120).map {
        ArtistEntity(
            it.toLong(),
            "Artist$it",
            (121..130).map { song ->
                SongEntity(
                    "mediaID$song",
                    "SongTitle$song",
                    "Artist$it",
                    "Uri$song",
                    (song + 10).toString(),
                    "Album$song",
                    "AlbumArt$song"
                )

            }.toMutableList(),
            (131..140).map { album ->
                AlbumEntity(
                    album.toLong(),
                    "Album$album",
                    "Artist$it",
                    "AlbumArt$album",
                )
            }.toMutableList()
        )
    }

    @Before
    fun create() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        brainzPlayerDatabase = Room
            .inMemoryDatabaseBuilder(context, BrainzPlayerDatabase::class.java)
            .build()
        albumDao = brainzPlayerDatabase.albumDao()
        artistDao = brainzPlayerDatabase.artistDao()
        playlistDao = brainzPlayerDatabase.playlistDao()
        songDao = brainzPlayerDatabase.songDao()
    }


    @Test
    fun albumDaoTests() = runTest {
        albumDao.addAlbums(albumEntities)
        albumDao.getAlbumEntity(1).test {
            val album = awaitItem()
            assert(album.albumId == (1).toLong())
            cancel()
        }
        albumDao.getAlbumEntities().test {
            val albumItems = awaitItem()
            assert(albumItems.size == 10)
            cancel()
        }
    }

    @Test
    fun playlistTests() = runTest {
        val playlistSize = playlistEntities.size
        playlistDao.insertPlaylists(playlistEntities)
        playlistDao.getPlaylist((102).toLong()).test {
            val playlist = awaitItem()
            assert(playlist.id == 102.toLong())
            assert(playlist.items.isNotEmpty())
            playlistDao.delete(playlist)

        }
        playlistDao.getAllPlaylist().test {
            val playlists = awaitItem()
            assert(playlists.size == playlistSize - 1)
            val playlist = playlists[0]
            val targetPlaylistSize = playlist.items.size
            playlistDao.renamePlaylistName("newName", playlist.id)
            playlistDao.updatePlaylistSongs(
                playlist.items.subList(0, targetPlaylistSize - 1),
                playlist.id
            )
            playlistDao.getPlaylist((101).toLong()).test {
                val thisPlaylist = awaitItem()
                assert(thisPlaylist.title == "newName")
                assert(thisPlaylist.items.size == targetPlaylistSize - 1)
            }
        }
    }

    @Test
    fun artistTests() = runTest {
        artistDao.addArtists(artistEntities)
        artistDao.getArtistEntity((111).toString()).test {
            val artist = awaitItem()
            assert(artist.songs.isNotEmpty())
            assert(artist.albums.isNotEmpty())
        }
        artistDao.getArtistEntities().test {
            val artists = awaitItem()
            assert(artists.size == 10)
        }
    }

    @Test
    fun songTest() = runTest {
        songDao.addSongs(songEntities)
        songDao.getSongEntity((11).toString()).test {
            val song = awaitItem()
            assert(song.mediaID == (11).toString())
        }
        songDao.getSongEntities().test {
            val songs = awaitItem()
            assert(songs.size == 10)
        }
    }

    @After
    fun cleanup() {
        brainzPlayerDatabase.close()
    }
}

@ExperimentalCoroutinesApi
class TestDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}