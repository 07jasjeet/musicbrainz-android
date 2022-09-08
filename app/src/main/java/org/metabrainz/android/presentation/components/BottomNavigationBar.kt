package org.metabrainz.android.presentation.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.thefinestartist.finestwebview.FinestWebView
import org.metabrainz.android.R
import org.metabrainz.android.presentation.features.brainzplayer.ui.BrainzPlayerActivity
import org.metabrainz.android.presentation.features.brainzplayer.ui.BrainzPlayerViewModel
import org.metabrainz.android.presentation.features.brainzplayer.ui.components.MarqueeText
import org.metabrainz.android.presentation.features.brainzplayer.ui.components.PlayPauseIcon
import org.metabrainz.android.presentation.features.brainzplayer.ui.components.SeekBar
import org.metabrainz.android.presentation.features.dashboard.DashboardActivity
import org.metabrainz.android.presentation.features.listens.ListensActivity
import org.metabrainz.android.presentation.features.login.LoginActivity
import org.metabrainz.android.presentation.features.navigation.BrainzNavigationItem
import org.metabrainz.android.presentation.features.navigation.NavigationItem
import org.metabrainz.android.util.BrainzPlayerExtensions.toSong


@ExperimentalPagerApi
@Composable
fun BottomNavigationBar(activity: Activity) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.BrainzPlayer,
        NavigationItem.Listens,
        NavigationItem.Critiques,
        NavigationItem.Profile,
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.app_bg),
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon),
                    modifier = Modifier.size(28.dp), contentDescription = item.title, tint = Color.Unspecified) },
                label = { Text(text = item.title, fontSize = 11.sp) },
                selectedContentColor = colorResource(id = R.color.white),
                unselectedContentColor = colorResource(id = R.color.gray),
                alwaysShowLabel = true,
                selected = true,
                onClick = {
                    when(item.route){
                        "home" -> {
                            val nextActivity = DashboardActivity::class.java
                            if(nextActivity != activity::class.java){
                                activity.startActivity(Intent(activity, DashboardActivity::class.java))
                            }
                        }
                        "brainzplayer" -> {
                            val nextActivity = BrainzPlayerActivity::class.java
                            if(nextActivity != activity::class.java){
                                activity.startActivity(Intent(activity, BrainzPlayerActivity::class.java))
                            }
                        }
                        "listens" -> {
                            val nextActivity = ListensActivity::class.java
                            if(nextActivity != activity::class.java){
                                activity.startActivity(Intent(activity, ListensActivity::class.java))
                            }
                        }
                        "critiques" -> {
                            FinestWebView.Builder(activity).show("https://critiquebrainz.org/")
                        }
                        "profile" -> {
                            val nextActivity = LoginActivity::class.java
                            if(nextActivity != activity::class.java){
                                activity.startActivity(Intent(activity, LoginActivity::class.java))
                            }
                        }
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun SongViewPager(viewModel: BrainzPlayerViewModel = hiltViewModel()) {
    val songList = viewModel.mediaItem.collectAsState().value.data
    val currentlyPlayingSong = viewModel.currentlyPlayingSong.collectAsState().value.toSong
    val pagerState = viewModel.pagerState.collectAsState().value
    val pageState = rememberPagerState(initialPage = pagerState)
    songList?.let{
        HorizontalPager(count = songList.size, state = pageState, modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.bp_bottom_song_viewpager)
            )) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    val progress by viewModel.progress.collectAsState()
                    SeekBar(
                        modifier = Modifier
                            .height(10.dp)
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        progress = progress,
                        onValueChange = viewModel::onSeek,
                        onValueChanged = viewModel::onSeeked
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                                .height(45.dp)
                                .width(45.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(shape = RoundedCornerShape(8.dp))
                                    .graphicsLayer { clip = true },
                                model = currentlyPlayingSong.albumArt,
                                contentDescription = "",
                                error = painterResource(
                                    id = R.drawable.ic_erroralbumart
                                ),
                                contentScale = ContentScale.Crop
                            )
                        }
                        val playIcon by viewModel.playButton.collectAsState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 35.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SkipPrevious,
                                    contentDescription = "",
                                    Modifier
                                        .size(35.dp)
                                        .clickable { viewModel.skipToPreviousSong() },
                                    tint = colorResource(
                                        id = R.color.bp_color_primary
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clip(CircleShape)
                                        .background(colorResource(id = R.color.bp_color_primary))
                                ) {
                                    PlayPauseIcon(
                                        playIcon,
                                        viewModel,
                                        Modifier.size(35.dp),
                                        tint = colorResource(
                                            id = R.color.bp_bottom_song_viewpager
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.SkipNext,
                                    contentDescription = "",
                                    Modifier
                                        .size(35.dp)
                                        .clickable { viewModel.skipToNextSong() },
                                    tint = colorResource(
                                        id = R.color.bp_color_primary
                                    )
                                )
                            }
                            MarqueeText(
                                text = currentlyPlayingSong.artist + " - " + currentlyPlayingSong.title,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = colorResource(
                                    id = R.color.white
                                )
                            )
                        }
                    }
                }
            }
        }
      //  TODO("Fix View Pager changing pages")
    }
}

@Composable
fun BrainzPlayerBottomBar( navController: NavController) {
    val items = listOf(
        BrainzNavigationItem.Home,
        BrainzNavigationItem.Songs,
        BrainzNavigationItem.Artists,
        BrainzNavigationItem.Albums,
        BrainzNavigationItem.Playlists,

        )
    val backStackEntry by navController.currentBackStackEntryAsState()
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.app_bg),
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 11.sp) },
                selectedContentColor = colorResource(id = R.color.white),
                unselectedContentColor = colorResource(id = R.color.gray),
                alwaysShowLabel = true,
                selected = selected,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}