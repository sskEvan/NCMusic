package com.ssk.ncmusic.ui.page.playmusic.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.player.PlayMode
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.SeekBar
import com.ssk.ncmusic.ui.page.playmusic.showPlayListSheet
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/11.
 */
@Composable
fun ColumnScope.CpnPlayMusicActionLayout() {
    MiddleActionLayout()
    ProgressLayout()
    BottomActionLayout()
}

@Composable
private fun MiddleActionLayout() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    LaunchedEffect(MusicPlayController.curRealIndex) {
        viewModel.getSongComment(MusicPlayController.realSongList[MusicPlayController.curRealIndex])
    }
    Row(
        modifier = Modifier
            .padding(start = 60.cdp, end = 60.cdp, bottom = 32.cdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiddleActionIcon(R.drawable.ic_like_no, modifier = Modifier.padding(end = 60.cdp))
        MiddleActionIcon(R.drawable.ic_download, modifier = Modifier.padding(end = 60.cdp))
        MiddleActionIcon(R.drawable.ic_action_sing, modifier = Modifier.padding(end = 60.cdp))
        Box(modifier = Modifier.width(138.cdp)) {
            MiddleActionIcon(R.drawable.ic_comment_count) {
                val json =
                    Uri.encode(Gson().toJson(MusicPlayController.realSongList[MusicPlayController.curRealIndex]))
                NCNavController.instance.navigate("${RouterUrls.SONG_COMMENT}/$json")
                scope.launch {
                    delay(300)
                    MusicPlayController.playMusicSheetOffset = ScreenUtil.getScreenHeight()
                }
            }
            viewModel.songCommentResult?.let {
                val commentText = StringUtil.friendlyNumber(it.total)
                Text(
                    text = commentText,
                    color = Color.White,
                    fontSize = 18.csp,
                    modifier = Modifier
                        .padding(top = 10.cdp, start = 52.cdp)
                        .align(Alignment.TopStart)
                )
            }
        }
        MiddleActionIcon(R.drawable.ic_song_more)
    }
}

@Composable
private fun MiddleActionIcon(
    resId: Int,
    modifier: Modifier = Modifier,
    clickable: () -> Unit = {}
) {
    CommonIcon(
        resId,
        tint = Color.White,
        modifier = modifier
            .size(78.cdp)
            .clip(CircleShape)
            .clickable {
                clickable.invoke()
            }
            .padding(16.cdp)
    )
}

@Composable
private fun ProgressLayout() {
    Row(
        modifier = Modifier
            .padding(start = 44.cdp, end = 44.cdp, bottom = 32.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = MusicPlayController.curPositionStr,
            fontSize = 26.csp,
            color = Color.White,
            modifier = Modifier.width(110.cdp)
        )
        SeekBar(
            progress = MusicPlayController.progress,
            seeking = {
                MusicPlayController.seeking(it)
            },
            seekTo = {
                MusicPlayController.seekTo(it)
            },
            modifier = Modifier.weight(1f)
        )
        Text(
            text = MusicPlayController.totalDuringStr,
            fontSize = 26.csp,
            color = Color.White,
            modifier = Modifier.width(110.cdp),
            textAlign = TextAlign.End
        )
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BottomActionLayout() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val coroutineScopeScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(start = 20.cdp, end = 20.cdp, bottom = 60.cdp)
            .fillMaxWidth()
            .height(120.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val playModeResId = when (MusicPlayController.playMode) {
            PlayMode.RANDOM -> R.drawable.ic_play_mode_random
            PlayMode.SINGLE -> R.drawable.ic_play_mode_single
            PlayMode.LOOP -> R.drawable.ic_play_mode_loop
        }
        BottomActionIcon(playModeResId) {
            when (MusicPlayController.playMode) {
                PlayMode.RANDOM -> MusicPlayController.changePlayMode(PlayMode.SINGLE)
                PlayMode.SINGLE -> MusicPlayController.changePlayMode(PlayMode.LOOP)
                PlayMode.LOOP -> MusicPlayController.changePlayMode(PlayMode.RANDOM)
            }
        }
        // 播放上一曲
        BottomActionIcon(R.drawable.ic_action_pre) {
            //sheetNeedleUp = true
            val newIndex = MusicPlayController.getPreRealIndex()
            Log.e("ssk", "播放上一曲 newIndex=${newIndex}")
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                // pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
            }
        }
        // 播放or暂停
        BottomActionIcon(
            if (MusicPlayController.isPlaying()) R.drawable.ic_action_pause else R.drawable.ic_action_play,
            size = 116
        ) {
            if (MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
                coroutineScopeScope.launch {
                    viewModel.sheetNeedleUp = true
                    viewModel.lastSheetDiskRotateAngleForSnap = viewModel.sheetDiskRotate.value
                    viewModel.sheetDiskRotate.stop()
                }
            } else {
                MusicPlayController.resume()
            }
        }
        // 播放下一曲
        BottomActionIcon(R.drawable.ic_action_next) {
            val newIndex = MusicPlayController.getNextRealIndex()
            Log.e("ssk", "播放下一曲 newIndex=${newIndex}")
            //sheetNeedleUp = true
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                //pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
            }
        }
        BottomActionIcon(R.drawable.ic_play_list) {
            showPlayListSheet = true
        }
    }
}


@Composable
private fun BottomActionIcon(
    resId: Int,
    size: Int = 84,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    CommonIcon(
        resId,
        tint = if (enable) Color.White else Color(0xFFBBBBBB),
        modifier = Modifier
            .size(size.cdp)
            .clip(CircleShape)
            .onClick(enableRipple = enable) {
                if (enable) {
                    onClick()
                }
            }
            .padding(16.cdp)
    )
}

