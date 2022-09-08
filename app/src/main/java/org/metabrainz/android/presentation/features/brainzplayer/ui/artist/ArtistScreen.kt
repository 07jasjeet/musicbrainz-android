package org.metabrainz.android.presentation.features.brainzplayer.ui.artist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.metabrainz.android.R
import org.metabrainz.android.data.sources.brainzplayer.Artist
import org.metabrainz.android.presentation.features.brainzplayer.ui.BrainzPlayerViewModel
import org.metabrainz.android.presentation.features.brainzplayer.ui.components.forwardingPainter


@Composable
fun ArtistScreen(navHostController: NavHostController) {
    val artistViewModel = hiltViewModel<ArtistViewModel>()
    val artists = artistViewModel.artists.collectAsState(initial = listOf())
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(artists.value) {
            Box(modifier = Modifier
                .padding(2.dp)
                .height(220.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    navHostController.navigate("onArtistClick/${it.id}")
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(CircleShape)
                            .size(150.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopCenter)
                                .background(colorResource(id = R.color.bp_bottom_song_viewpager)),
                            imageVector = Icons.Rounded.Person,
                            colorFilter = ColorFilter.tint(colorResource(id = R.color.gray)),
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = it.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(
                            id = R.color.white
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OnArtistClickScreen(artistID: String, navHostController: NavHostController) {
    val brainzPlayerViewModel = hiltViewModel<BrainzPlayerViewModel>()
    val artistViewModel = hiltViewModel<ArtistViewModel>()
    val artist = artistViewModel.getArtistByID(artistID).collectAsState(initial = Artist()).value
    val artistAlbums =
        artistViewModel.getAllAlbumsOfArtist(artist).collectAsState(initial = listOf()).value.distinctBy { it.albumId }
    val artistSongs = artistViewModel.getAllSongsOfArtist(artist).collectAsState(initial = listOf()).value.distinctBy { it.mediaID }

    LazyColumn {
        item {
            Text(
                text = "Albums by the Artist",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white)
            )
        }
        item {
            LazyRow {
                items(items = artistAlbums) {
                    Box(
                        modifier = Modifier
                            .height(240.dp)
                            .width(200.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                navHostController.navigate("onAlbumClick/${it.albumId}")
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(CircleShape)
                                    .background(color = colorResource(id = R.color.bp_bottom_song_viewpager))
                                    .size(150.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.TopCenter)
                                        .clip(CircleShape),
                                    model = it.albumArt,
                                    contentDescription = "",
                                    error = forwardingPainter(
                                        painter = painterResource(id = R.drawable.ic_album)
                                    ) { info ->
                                        inset(25f, 25f) {
                                            with(info.painter) {
                                                draw(size, info.alpha, info.colorFilter)
                                            }
                                        }
                                    },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Text(
                                text = it.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = colorResource(id = R.color.white)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Songs by the Artist",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white)
            )
        }
        items(artistSongs){
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.98f)
                    .clickable {
                        brainzPlayerViewModel.playOrToggleSong(it, true)
                    },
                backgroundColor = MaterialTheme.colors.onSurface
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Row(horizontalArrangement = Arrangement.Start) {
                    AsyncImage(
                        model = it.albumArt,
                        contentDescription = "",
                        error = painterResource(
                            id = R.drawable.ic_erroralbumart
                        ),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.size(70.dp)
                    )
                    Column(Modifier.padding(start = 10.dp)) {
                        androidx.compose.material.Text(
                            text = it.title,
                            color = Color.White
                        )
                        androidx.compose.material.Text(
                            text = it.artist,
                            color = Color.White
                        )
                    }
                }
            }

        }
    }
}
