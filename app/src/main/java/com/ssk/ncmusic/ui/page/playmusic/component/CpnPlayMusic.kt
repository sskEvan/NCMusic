package com.ssk.ncmusic.ui.page.playmusic.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel

/**
 * Created by ssk on 2022/4/25.
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CpnPlayMusic(backCallback: () -> Unit) {
    Log.d("ssk", "PlayMusicContent recompose")

    val viewModel: PlayMusicViewModel = hiltViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.DarkGray.copy(alpha = 0.8f),
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
            .onClick(enableRipple = false) {},
        contentAlignment = Alignment.Center
    ) {
        val curSong = MusicPlayController.realSongList[MusicPlayController.curRealIndex]
        BlurBackground(curSong)
        Column(modifier = Modifier.fillMaxSize()) {
            CommonTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                customTitleLayout = {
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            text = curSong.name,
                            fontSize = 36.csp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = curSong.ar[0].name,
                            fontSize = 24.csp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.cdp)
                        )
                    }

                },
                leftIconResId = R.drawable.ic_arrow_down,
                leftClick = { backCallback() },
                backgroundColor = Color.Transparent,
                contentColor = Color.White
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (!viewModel.showLyric) {
                        CpnDiskPager()
                    } else {
                        CpnLyric()
                    }
                }

                CpnPlayMusicActionLayout()
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BlurBackground(song: SongBean) {
    // 高斯模糊背景
    Image(
        painter = rememberImagePainter(
            song.al.picUrl,
            builder = {
                transformations(BlurTransformation(LocalContext.current, 18f, 5f))
            }
        ),
        contentDescription = "disc_background",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.5f }
    )
}


