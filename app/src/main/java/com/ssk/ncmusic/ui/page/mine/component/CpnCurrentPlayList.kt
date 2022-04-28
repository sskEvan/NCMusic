package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.player.PlayMode
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.page.showPlayListSheet
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick

/**
 * Created by ssk on 2022/4/25.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CpnCurrentPlayList() {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(showPlayListSheet) {
        if (showPlayListSheet) {
            lazyListState.animateScrollToItem(MusicPlayController.curOriginIndex)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(0.cdp, 1080.cdp)
            .clip(RoundedCornerShape(topStart = 40.cdp, topEnd = 40.cdp))
            .background(AppColorsProvider.current.pure)
            .padding(top = 48.cdp),
    ) {
        PlayListHeader()

        LazyColumn(
            modifier = Modifier
                .padding(top = 32.cdp)
                .fillMaxWidth(),
            state = lazyListState
        ) {
            itemsIndexed(MusicPlayController.originSongList) { index, item ->
                PlayListItem(index, MusicPlayController.originSongList[index])
            }
        }
    }
}

@Composable
private fun PlayListHeader() {
    Row(
        modifier = Modifier
            .padding(start = 48.cdp, end = 32.cdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "当前播放",
                fontSize = 36.csp, fontWeight = FontWeight.Bold, color = AppColorsProvider.current.firstText
            )
            Text(
                text = "(${MusicPlayController.originSongList.size})",
                fontSize = 28.csp, fontWeight = FontWeight.Bold, color = AppColorsProvider.current.secondText
            )
        }

        Row(
            modifier = Modifier.onClick {
                when(MusicPlayController.playMode) {
                    PlayMode.RANDOM -> MusicPlayController.changePlayMode(PlayMode.SINGLE)
                    PlayMode.SINGLE -> MusicPlayController.changePlayMode(PlayMode.LOOP)
                    PlayMode.LOOP -> MusicPlayController.changePlayMode(PlayMode.RANDOM)
                }
            }.padding(horizontal = 16.cdp, vertical = 8.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val playModeText = when (MusicPlayController.playMode) {
                PlayMode.RANDOM -> "随机播放"
                PlayMode.SINGLE -> "单曲循环"
                PlayMode.LOOP -> "列表循环"
            }
            val playModeResId = when (MusicPlayController.playMode) {
                PlayMode.RANDOM -> R.drawable.ic_play_mode_random
                PlayMode.SINGLE -> R.drawable.ic_play_mode_single
                PlayMode.LOOP -> R.drawable.ic_play_mode_loop
            }
            Text(
                text = playModeText,
                fontSize = 32.csp,
                color = AppColorsProvider.current.firstText,
                modifier = Modifier.padding(end = 16.cdp)
            )
            CommonIcon(
                resId = playModeResId,
                modifier = Modifier.size(36.cdp),
                tint = AppColorsProvider.current.firstText
            )
        }
    }
}

@Composable
private fun PlayListItem(index: Int, songBean: SongBean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp)
            .onClick {
                MusicPlayController.playByOriginIndex(index)
            }
            .padding(horizontal = 48.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (MusicPlayController.isPlaying(songBean)) {
            CpnPlayingMark(playing = MusicPlayController.isPlaying(), modifier = Modifier.padding(end = 32.cdp))
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (!MusicPlayController.isPlaying(songBean)) AppColorsProvider.current.firstText else AppColorsProvider.current.primary,
                        fontSize = 32.csp
                    )
                ) {
                    append(songBean.name)
                }
                withStyle(
                    style = SpanStyle(
                        color = if (!MusicPlayController.isPlaying(songBean)) AppColorsProvider.current.secondText else AppColorsProvider.current.secondary,
                        fontSize = 24.csp
                    )
                ) {
                    append(" - ${songBean.ar[0].name}")
                }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}



