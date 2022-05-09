package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.CommonHeadBackgroundShape
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.page.mine.component.CpnSongItem
import com.ssk.ncmusic.ui.page.mine.component.cpnBottomMusicPlayPadding
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.mine.PlayListViewModel
import me.onebone.toolbar.*

/**
 * Created by ssk on 2022/4/21.
 */
@Composable
fun PlaylistPage(playlistBean: PlaylistBean) {
    Log.e("ssk", "PlaylistPage recompose !!!")
    val viewModel: PlayListViewModel = hiltViewModel()
    viewModel.playlistBean = playlistBean

    val state = rememberCollapsingToolbarScaffoldState()
    val showPlayListTitleThreshold = (1 - state.toolbarState.progress) >= (LocalWindowInsets.current.statusBars.top + 188.cdp.toPx) / 584.cdp.toPx
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            ScrollHeader(playlistBean, state, if (showPlayListTitleThreshold) playlistBean.name else "歌单")
        }
    ) {
        Body()
    }
}


@Composable
private fun CollapsingToolbarScope.ScrollHeader(playlistBean: PlaylistBean, toolbarState: CollapsingToolbarScaffoldState, title: String) {
    Log.d("ssk", "PlayListPage ScrollHeader  recompose ")
    val headCountInfoLayoutChangeAlphaThreshold = remember { 1 - (584f - 88) / 584 }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(584.cdp)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CommonHeadBackgroundShape(toolbarState.toolbarState.progress * 80))
                .background(brush = Brush.linearGradient(listOf(Color.Gray.copy(0.7f), Color.LightGray.copy(0.7f), Color.Gray.copy(0.7f))))
        ) {
            HeadBackground(playlistBean)
            HeadPlayListInfo(
                modifier = Modifier.graphicsLayer { alpha = toolbarState.toolbarState.progress },
                playlistBean
            )
        }
        HeadCountInfoLayout(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    val alphaValue = toolbarState.toolbarState.progress
                    if (alphaValue < headCountInfoLayoutChangeAlphaThreshold) {
                        alpha = toolbarState.toolbarState.progress
                    }
                }, playlistBean
        )
    }

    //Column {
    CommonTopAppBar(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(88.cdp),
        backgroundColor = Color.Transparent,
        title = title,
        contentColor = Color.White,
        rightIconResId = R.drawable.ic_search
    )
    //}
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun HeadBackground(playlistBean: PlaylistBean) {
    //Log.d("ssk", "PlayListPage HeadBackground  recompose ")
    Image(
        rememberImagePainter(playlistBean.coverImgUrl,
            builder = { transformations(BlurTransformation(LocalContext.current, 10f, 10f)) }
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(584.cdp)
            .graphicsLayer { alpha = 0.5f },
    )
}

@Composable
private fun HeadPlayListInfo(modifier: Modifier, playlistBean: PlaylistBean) {
    //Log.d("ssk", "PlayListPage HeadPlayListInfo  recompose ")
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 132.cdp)
            .fillMaxSize()
            .padding(horizontal = 32.cdp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonNetworkImage(
            url = playlistBean.coverImgUrl, modifier = Modifier
                .size(240.cdp)
                .clip(RoundedCornerShape(16.cdp))
        )
        Column(
            modifier = Modifier
                .padding(start = 32.cdp)
                .weight(1f)
                .height(240.cdp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = playlistBean.name, color = Color.White, fontWeight = FontWeight.Medium, maxLines = 2, fontSize = 28.csp)

            Row(
                modifier = Modifier.height(88.cdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CommonNetworkImage(
                    url = AppGlobalData.sLoginResult.profile.avatarUrl,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier
                        .size(50.cdp)
                        .clip(RoundedCornerShape(50))
                )
                Text(
                    text = AppGlobalData.sLoginResult.profile.nickname,
                    fontSize = 28.csp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.cdp)
                )
            }

            Text(text = playlistBean.description ?: "暂无描述", color = Color.White, fontSize = 28.csp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun HeadCountInfoLayout(modifier: Modifier, playlistBean: PlaylistBean) {
    //Log.d("ssk", "PlayListPage HeadCountInfoLayout  recompose ")
    Row(
        modifier = modifier
            .padding(start = 32.cdp, end = 32.cdp, bottom = 4.cdp)
            .height(80.cdp)
            .fillMaxWidth()
            .padding(horizontal = 16.cdp)
            .shadow(2.dp, RoundedCornerShape(50))
            .background(AppColorsProvider.current.card),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HeaderCountInfoItem(
            R.drawable.ic_action_play,
            "播放(${StringUtil.friendlyNumber(playlistBean.playCount)})",
            true
        )
        HeaderCountInfoItem(
            R.drawable.ic_comment_count,
            "评论(${StringUtil.friendlyNumber(playlistBean.commentCount)})",
            true
        )
        HeaderCountInfoItem(
            R.drawable.ic_share,
            "分享(${StringUtil.friendlyNumber(playlistBean.shareCount)})",
            false
        )
    }
}

@Composable
private fun RowScope.HeaderCountInfoItem(iconRedId: Int, text: String, showDivider: Boolean) {
    Row(modifier = Modifier.weight(1f)) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(iconRedId),
                "",
                tint = AppColorsProvider.current.firstIcon,
                modifier = Modifier
                    .size(40.cdp)
                    .padding(end = 8.cdp)
            )
            Text(
                text = text,
                fontSize = 24.csp,
                color = AppColorsProvider.current.firstText,
            )
        }
        if (showDivider) {
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(40.cdp)
                    .width(2.cdp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body() {
    Log.e("ssk", "PlayListPage Body  recompose ")
    val viewModel: PlayListViewModel = hiltViewModel()
    val paddingBottom = if (MusicPlayController.showCpnBottomMusicPlay) {
        cpnBottomMusicPlayPadding
    } else {
        0.dp
    }
    ViewStateComponent(
        viewStateLiveData = viewModel.songDetailResult,
        loadDataBlock = {
            viewModel.getSongDetail()
        },
        viewStateComponentModifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        viewStateContentAlignment = BiasAlignment(0f, -0.6f)
    ) { data ->
        CompositionLocalProvider(LocalOverScrollConfiguration.provides(null)) {
            Column {
                PlayListHeader(viewModel.playlistBean)
                Divider(Modifier.fillMaxWidth(), thickness = 1.cdp, color = Color.LightGray)
                LazyColumn(modifier = Modifier.padding(bottom = paddingBottom)) {
                    itemsIndexed(data.songs) { index, item ->
                        CpnSongItem(index, item) {
                            MusicPlayController.setDataSource(viewModel.songList, index)
                            MusicPlayController.showCpnBottomMusicPlay = false
                            MusicPlayController.showPlayMusicSheet = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayListHeader(playlistBean: PlaylistBean) {
    val viewModel: PlayListViewModel = hiltViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp)
            .onClick {
                MusicPlayController.setDataSource(viewModel.songList, 0)
                MusicPlayController.showCpnBottomMusicPlay = false
                MusicPlayController.showPlayMusicSheet = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonIcon(
            R.drawable.ic_play_list_header_play,
            tint = AppColorsProvider.current.primary,
            modifier = Modifier
                .padding(horizontal = 32.cdp)
                .size(50.cdp)
        )

        Text(
            text = "播放全部",
            fontSize = 32.csp,
            fontWeight = FontWeight.Bold,
            color = AppColorsProvider.current.firstText,
        )
        Text(
            text = "(${playlistBean.trackCount})",
            fontSize = 28.csp,
            color = AppColorsProvider.current.secondText,
        )
    }
}