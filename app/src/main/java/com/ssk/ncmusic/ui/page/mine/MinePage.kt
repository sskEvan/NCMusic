package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.page.mine.component.CpnMusicApplication
import com.ssk.ncmusic.ui.page.mine.component.CpnPlayListPlaceHolder
import com.ssk.ncmusic.ui.page.mine.component.CpnSongPlayListHelper
import com.ssk.ncmusic.ui.page.mine.component.CpnUserInfo
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.mine.MineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.*

/**
 * Created by ssk on 2022/4/17.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinePage(drawerState: DrawerState) {

    val viewModel: MineViewModel = hiltViewModel()
    var bodyAlphaValue by remember { mutableStateOf(1f) }
    val topBarAlphaState = remember { mutableStateOf(0f) }

    val lazyListState = rememberLazyListState()
    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex

    if (!animateScrolling && viewModel.songHelperIndex != 0 && lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
        when {
            lazyListState.layoutInfo.visibleItemsInfo.last().index == viewModel.songHelperIndex &&
                    lazyListState.layoutInfo.visibleItemsInfo.last().offset == lazyListState.layoutInfo.viewportSize.height - lazyListState.layoutInfo.visibleItemsInfo.last().size -> {
                viewModel.selectedTabIndex = 2
            }
            (firstVisibleItemIndex == viewModel.collectPlayListHeaderIndex - 1
                    && lazyListState.firstVisibleItemScrollOffset >= lazyListState.layoutInfo.visibleItemsInfo[1].size - 100.cdp.toPx) ||
                    firstVisibleItemIndex > viewModel.collectPlayListHeaderIndex - 1 -> {
                viewModel.selectedTabIndex = 1
            }
            firstVisibleItemIndex >= viewModel.selfCreatePlayListHeaderIndex -> {
                viewModel.selectedTabIndex = 0
            }
        }
    }
    CompositionLocalProvider(LocalOverScrollConfiguration.provides(null)) {
        Box(modifier = Modifier.fillMaxSize()) {
            ViewStateComponent(viewStateLiveData = viewModel.userPlaylistResult,
                loadDataBlock = { viewModel.getUserPlayList() }) {

                Box {

                    val dragToggleState = rememberDragToggleState(viewModel.dragStatus)

                    if (dragToggleState.isDragging) {
                        animateScrolling = false
                    }
                    FixHeadBackgroundDraggableBodyLayout(
                        state = dragToggleState,
                        triggerRadio = 0.24f,
                        maxDragRadio = 0.48f,
                        onOverOpenTriggerWhenDragging = {
                            viewModel.dragStatus = DragStatus.OverOpenTriggerWhenDragging
                            viewModel.vibrator()
                        },
                        onOverOpenTriggerWhenFling = {
                            viewModel.dragStatus = DragStatus.OverOpenTriggerWhenFling
                        },
                        onOpened = {
                            viewModel.dragStatus = DragStatus.Opened
                            NCNavController.instance.navigate(RouterUrls.PROFILE)
                            viewModel.dragStatus = DragStatus.Idle
                        },
                        headBackgroundComponent = { state, _, maxDrag ->
                            if (state.offset >= 0) {
                                var alpha = state.offset / maxDrag
                                if (alpha > 1f) {
                                    alpha = 1f
                                }
                                bodyAlphaValue = alpha
                            }
                            HeaderBackground(bodyAlphaValue)
                        }) {

                        Body(topBarAlphaState, lazyListState, dragToggleState, 1 - bodyAlphaValue)
                    }
                }
            }

            TopBar(topBarAlphaState, drawerState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    topBarAlphaState: MutableState<Float>,
    lazyListState: LazyListState,
    dragToggleState: DragToggleState,
    bodyAlphaValue: Float,
) {
    val localWindowInsets = LocalWindowInsets.current
    val stickyPositionTop = remember { localWindowInsets.statusBars.top + STICKY_TAB_LAYOUT_HEIGHT.toPx }
    val toolbarMaxHeight = remember {
        localWindowInsets.statusBars.top.transformDp + STICKY_TAB_LAYOUT_HEIGHT + 300.cdp +  // 状态栏高度+标题栏高度+用户信息高度
                368.cdp +  // 音乐应用高度
                202.cdp // 喜欢的歌单高度
    }
    val toolbarMaxHeightPx = remember { toolbarMaxHeight.toPx }

    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    var topBarAlpha = (1 - toolbarScaffoldState.toolbarState.progress) / (stickyPositionTop / toolbarMaxHeightPx)
    if (topBarAlpha > 1) topBarAlpha = 1f
    topBarAlphaState.value = topBarAlpha
    Log.e("ssk", "topBarAlphaState.value = ${topBarAlphaState.value}")
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(if (dragToggleState.offset > 0) Color.Transparent else AppColorsProvider.current.background),
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            ScrollHeader(bodyAlphaValue, toolbarMaxHeight)
        }
    ) {
        PlayList(bodyAlphaValue, lazyListState, toolbarScaffoldState.toolbarState)
    }
}

@Composable
private fun CollapsingToolbarScope.ScrollHeader(bodyAlphaValue: Float, toolbarMaxHeight: Dp) {
    val viewModel: MineViewModel = hiltViewModel()

    Column(
        Modifier
            .fillMaxWidth()
            .height(toolbarMaxHeight)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        CpnUserInfo(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 88.cdp)
                .onClick(enableRipple = false) {
                    viewModel.vibrator()
                    viewModel.dragStatus = DragStatus.OverOpenTriggerWhenFling
                }
        )

        Box(
            modifier = Modifier
                .graphicsLayer { alpha = bodyAlphaValue }
                .mineCommonCard()
                .height(300.cdp),
            contentAlignment = Alignment.Center
        ) {
            CpnMusicApplication()
        }

        Box(
            modifier = Modifier
                .graphicsLayer { alpha = bodyAlphaValue }
                .padding(bottom = 12.cdp)
                .height(190.cdp)
                .mineCommonCard(),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.favoritePlayList != null) {
                CpnUserPlayListItem(viewModel.favoritePlayList, 0.cdp)
            } else {
                Text(
                    text = "暂时没有喜欢的歌单",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 28.csp
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalWindowInsets.current.statusBars.top.transformDp + 88.cdp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayList(
    bodyAlphaValue: Float,
    lazyListState: LazyListState,
    toolbarState: CollapsingToolbarState
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: MineViewModel = hiltViewModel()

    Log.e("ssk", "body inner recompose")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = bodyAlphaValue },
        state = lazyListState
    ) {

        // tabLayout
        stickyHeader {
            StickyTabLayout(lazyListState, coroutineScope, bodyAlphaValue, toolbarState)
        }

        // 创建歌单
        item {
            PlaylistHeader(title = "创建歌单(${viewModel.selfCreatePlayList?.size ?: 0})")
        }

        if (viewModel.selfCreatePlayList?.size ?: 0 > 0) {
            items(viewModel.selfCreatePlayList!!.size - 1) {
                CpnUserPlayListItem(viewModel.selfCreatePlayList!![it])
            }

            // 创建歌单 footer
            item {
                PlaylistFooter(viewModel.selfCreatePlayList!!.last())
            }
        } else {
            item {
                CpnPlayListPlaceHolder(tip = "暂时没有创建的歌单")
            }
        }

        // 收藏歌单 header
        item {
            PlaylistHeader(title = "收藏歌单(${viewModel.collectPlayList?.size ?: 0})")
        }

        if (viewModel.collectPlayList?.size ?: 0 > 0) {
            items(viewModel.collectPlayList!!.size - 1) {
                CpnUserPlayListItem(viewModel.collectPlayList!![it])
            }

            // 收藏歌单 footer
            item {
                PlaylistFooter(viewModel.collectPlayList!!.last())
            }
        } else {
            item {
                CpnPlayListPlaceHolder(tip = "暂时没有收藏的歌单")
            }
        }

        // 歌单助手
        item {
            Box(
                modifier = Modifier
                    .padding(bottom = 30.cdp)
                    .mineCommonCard(),
                contentAlignment = Alignment.Center
            ) {
                CpnSongPlayListHelper()
            }
        }
    }
}

@Composable
private fun PlaylistHeader(title: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 32.cdp, end = 32.cdp, top = 20.cdp)
            .background(
                AppColorsProvider.current.card,
                RoundedCornerShape(topStart = 24.cdp, topEnd = 24.cdp)
            )
            .padding(top = 24.cdp)
    ) {
        Text(
            text = title,
            color = AppColorsProvider.current.secondText,
            fontSize = 28.csp,
            modifier = Modifier.padding(bottom = 12.dp, start = 32.cdp)
        )
    }

}

@Composable
private fun PlaylistFooter(platListBean: PlaylistBean) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 24.cdp, bottomEnd = 24.cdp))
    ) {
        CpnUserPlayListItem(platListBean)
        Box(
            Modifier
                .padding(start = 32.cdp, end = 32.cdp)
                .fillMaxWidth()
                .height(24.cdp)
                .background(
                    AppColorsProvider.current.pure,
                    RoundedCornerShape(bottomStart = 24.cdp, bottomEnd = 24.cdp)
                )
        )
    }
}

@OptIn(ExperimentalToolbarApi::class)
@Composable
private fun StickyTabLayout(
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    bodyAlphaValue: Float,
    state: CollapsingToolbarState
) {
    val viewModel: MineViewModel = hiltViewModel()

    Surface(color = Color.Transparent) {
        Log.e("ssk", "state.progress=${state.progress}")
        val backgroundColor = if (state.progress > 0.001)
            AppColorsProvider.current.background else AppColorsProvider.current.pure
        //val backgroundColor = Color.Transparent
        CommonTabLayout(
            tabTexts = tabs,
            backgroundColor = backgroundColor,
            style = CommonTabLayoutStyle(isScrollable = false,
                indicatorPaddingBottom = 18.cdp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(STICKY_TAB_LAYOUT_HEIGHT)
                    .background(backgroundColor)
                    .graphicsLayer { alpha = bodyAlphaValue },
                tabItemDrawBehindBlock = { position ->
                    if (position != tabs.size - 1) {
                        drawLine(
                            Color.LightGray,
                            Offset(size.width, size.height * 0.3f),
                            Offset(size.width, size.height * 0.7f),
                            strokeWidth = 2.cdp.toPx()
                        )
                    }
                }
            ),
            selectedIndex = viewModel.selectedTabIndex
        ) {

            viewModel.selectedTabIndex = it

            animateScrolling = true
            coroutineScope.launch {
                if (state.progress != 0f) {
                    state.collapse(100)
                }
                when (it) {
                    0 -> lazyListState.animateScrollToItem(viewModel.selfCreatePlayListHeaderIndex)
                    1 -> lazyListState.animateScrollToItem(viewModel.collectPlayListHeaderIndex, -STICKY_TAB_LAYOUT_HEIGHT.toPx.toInt())
                    else -> lazyListState.animateScrollToItem(viewModel.songHelperIndex)
                }
                animateScrolling = false
            }
        }
    }
}


@Composable
private fun TopBar(topBarAlphaState: MutableState<Float>, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    CommonTopAppBar(
        modifier = Modifier
            .background(AppColorsProvider.current.pure.copy(alpha = topBarAlphaState.value))
            .statusBarsPadding(),
        backgroundColor = Color.Transparent,
        leftIconResId = R.drawable.ic_drawer_toggle,
        leftClick = {
            scope.launch {
                if (drawerState.isOpen) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        },
        rightIconResId = R.drawable.ic_search
    )
    AnimatedVisibility(
        modifier = Modifier.statusBarsPadding(),
        visibleState = remember { MutableTransitionState(false) }
            .apply { targetState = topBarAlphaState.value == 1f },
        enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = ExitTransition.None
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CommonNetworkImage(
                url = AppGlobalData.sLoginResult!!.profile.avatarUrl,
                placeholder = R.drawable.ic_default_avator,
                error = R.drawable.ic_default_avator,
                modifier = Modifier
                    .size(50.cdp)
                    .clip(
                        RoundedCornerShape(50)
                    )
            )
            Text(
                text = AppGlobalData.sLoginResult!!.profile.nickname,
                fontSize = 32.csp,
                fontWeight = FontWeight.Medium,
                color = AppColorsProvider.current.firstText,
                modifier = Modifier.padding(start = 20.cdp)
            )
        }
    }
}

@Composable
private fun HeaderBackground(alphaValue: Float) {
    CommonNetworkImage(
        url = AppGlobalData.sLoginResult?.profile?.backgroundUrl,
        modifier = Modifier
            .fillMaxWidth()
            .height(584.cdp)
            .clip(CommonHeadBackgroundShape())
            .graphicsLayer {
                alpha = alphaValue
            },
        error = R.drawable.ic_bg,
        contentScale = ContentScale.FillBounds
    )
}

fun Modifier.mineCommonCard() = composed {
    this
        .fillMaxWidth()
        .padding(start = 32.cdp, end = 32.cdp, top = 20.cdp)
        .background(AppColorsProvider.current.card, RoundedCornerShape(24.cdp))
        .padding(top = 24.cdp, bottom = 24.cdp)
}

private var animateScrolling = false
private val STICKY_TAB_LAYOUT_HEIGHT = 88.cdp
private val tabs = listOf("创建歌单", "收藏歌单", "歌单助手")




