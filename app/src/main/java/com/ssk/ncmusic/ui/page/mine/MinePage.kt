package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.Routes
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.toPx
import com.ssk.ncmusic.viewmodel.mine.MineViewModel
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/17.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinePage() {
    val viewModel: MineViewModel = hiltViewModel()
    var bodyAlphaValue by remember { mutableStateOf(1f) }
    val topBarAlphaValue = remember { mutableStateOf(0f) }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val localWindowInsets = LocalWindowInsets.current
    val stickyPositionTop = remember { localWindowInsets.statusBars.top + 100.cdp.toPx }

    var topBarAlpha = scrollState.value / stickyPositionTop
    if (topBarAlpha > 1) topBarAlpha = 1f
    topBarAlphaValue.value = topBarAlpha

    if (!animateScrolling) {
        for (i in itemPositionMap.size - 1 downTo 0) {
            if (scrollState.value + stickyPositionTop > itemPositionMap[i]!!) {
                selectedTabIndex.value = i
                break
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ViewStateComponent(viewStateLiveData = viewModel.userPlaylistResult,
            loadDataBlock = { viewModel.getUserPlayList() }) {

            Box {
                var dragStatus by remember {
                    mutableStateOf<DragStatus>(DragStatus.Idle)
                }
                val dragToggleState = rememberDragToggleState(dragStatus)

                if (dragToggleState.isDraggableInProgress) {
                    animateScrolling = false
                }
                FixHeadBackgroundDraggableBodyLayout(
                    state = dragToggleState,
                    triggerRadio = 0.24f,
                    maxDragRadio = 0.48f,
                    onOverOpenTrigger = {
                        dragStatus = DragStatus.OverOpenTrigger
                    },
                    onOpened = {
                        dragStatus = DragStatus.Opened
                        NCNavController.instance.navigate(Routes.PROFILE)
                        dragStatus = DragStatus.Idle
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

                    Body(1 - bodyAlphaValue, selectedTabIndex, scrollState)
                }
            }
        }

        TopBar(topBarAlphaValue.value)
    }
}


private const val KEY_TAB_LAYOUT = -1
private const val KEY_CREATE_PLAY_LIST = 0
private const val KEY_COLLECT_PLAY_LIST = 1
private const val KEY_PLAY_LIST_HELP = 2

private var animateScrolling = false
private val itemPositionMap = HashMap<Int, Float>()

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    bodyAlphaValue: Float,
    selectedTabIndex: MutableState<Int>,
    scrollState: ScrollState,
) {
    val coroutineScope = rememberCoroutineScope()
    val localWindowInsets = LocalWindowInsets.current
    val stickyPositionTop = remember { localWindowInsets.statusBars.top + 100.cdp.toPx }
    val stickyPositionBottom = remember { localWindowInsets.statusBars.top + 188.cdp.toPx }
    var showStickyTabLayout by remember { mutableStateOf(false) }

    val viewModel: MineViewModel = hiltViewModel()

    Box {
        Log.e("ssk", "body inner recompose")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            // 用户信息
            UserInfoComponent(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 88.cdp)
            )

            // 音乐应用
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = bodyAlphaValue }
                    .mineCommonCard()
                    .height(300.cdp),
                contentAlignment = Alignment.Center
            ) {
                MusicApplicationComponent()
            }

            // 喜欢的歌单
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = bodyAlphaValue }
                    .mineCommonCard(),
                contentAlignment = Alignment.Center
            ) {
                UserPlaylistItem(viewModel.favoritePlayList)
            }

            // tabLayout
            CommonTabLayout(
                tabTexts = tabs,
                backgroundColor = Color.Transparent,
                style = CommonTabLayoutStyle(isScrollable = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.cdp)
                        .padding(top = 12.cdp)
                        .onGloballyPositioned {
                            if (itemPositionMap[KEY_TAB_LAYOUT] == null && itemPositionMap[KEY_TAB_LAYOUT] == 0f) {
                                itemPositionMap[KEY_TAB_LAYOUT] = it.boundsInParent().top
                            }
                            showStickyTabLayout = it.positionInRoot().y <= stickyPositionTop
                        }
                        .graphicsLayer { alpha = bodyAlphaValue }
                ),
                selectedIndex = selectedTabIndex.value
            ) {

                selectedTabIndex.value = it
                itemPositionMap[it]?.let { position ->
                    animateScrolling = true
                    coroutineScope.launch {
                        scrollState.animateScrollTo((position - stickyPositionBottom).toInt(), tween(500))
                        animateScrolling = false
                    }
                }
            }

            // 创建歌单
            UserPlaylistComponent(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = bodyAlphaValue
                    }
                    .onGloballyPositioned {
                        if (itemPositionMap[KEY_CREATE_PLAY_LIST] == null || itemPositionMap[KEY_CREATE_PLAY_LIST] == 0f) {
                            itemPositionMap[KEY_CREATE_PLAY_LIST] = it.boundsInParent().top
                        }
                    },
                list = viewModel.selfCreatePlayList,
                title = "创建歌单"
            )

            // 收藏歌单
            UserPlaylistComponent(
                modifier = Modifier
                    .graphicsLayer { alpha = bodyAlphaValue }
                    .onGloballyPositioned {
                        if (itemPositionMap[KEY_COLLECT_PLAY_LIST] == null || itemPositionMap[KEY_COLLECT_PLAY_LIST] == 0f) {
                            itemPositionMap[KEY_COLLECT_PLAY_LIST] = it.boundsInParent().top
                        }
                    },
                list = viewModel.collectPlayList,
                title = "收藏歌单"
            )

            // 歌单助手
            Box(
                modifier = Modifier
                    .padding(bottom = 30.cdp)
                    .mineCommonCard()
                    .height(500.cdp)
                    .onGloballyPositioned {
                        if (itemPositionMap[KEY_PLAY_LIST_HELP] == null || itemPositionMap[KEY_PLAY_LIST_HELP] == 0f) {
                            itemPositionMap[KEY_PLAY_LIST_HELP] = it.boundsInParent().top
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("歌单助手", color = AppColorsProvider.current.firstText)
            }
        }

        if (showStickyTabLayout) {
            CommonTabLayout(
                tabTexts = tabs,
                backgroundColor = AppColorsProvider.current.pure,
                style = CommonTabLayoutStyle(
                    isScrollable = false,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 88.cdp)
                        .fillMaxWidth()
                        .height(100.cdp)
                        .background(AppColorsProvider.current.pure)
                        .padding(top = 12.cdp)
                ),
                selectedIndex = selectedTabIndex.value
            ) {
                selectedTabIndex.value = it
                itemPositionMap[it]?.let { position ->
                    animateScrolling = true
                    coroutineScope.launch {
                        scrollState.animateScrollTo((position - stickyPositionBottom).toInt(), tween(500))
                        animateScrolling = false
                    }
                }
            }
        }
    }
}

@Composable
private fun UserPlaylistComponent(
    modifier: Modifier = Modifier,
    list: List<PlaylistBean>?,
    title: String
) {

    list?.let {
        Box(
            modifier = modifier.mineCommonCard()
        ) {
            Column {
                Text(
                    text = "${title}(${list.size}个)",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp, top = 20.cdp, start = 32.cdp)
                )
                it.forEach {
                    UserPlaylistItem(it)
                }
            }
        }
    }
}

@Composable
private fun TopBar(alphaValue: Float) {
    CommonTopAppBar(
        modifier = Modifier
            .background(AppColorsProvider.current.pure.copy(alpha = alphaValue))
            .statusBarsPadding(),
        backgroundColor = Color.Transparent,
        leftIconResId = R.drawable.ic_drawer_toggle,
        leftClick = { },
        rightIconResId = R.drawable.ic_search
    )
    AnimatedVisibility(
        modifier = Modifier.statusBarsPadding(),
        visibleState = remember { MutableTransitionState(false) }
            .apply { targetState = alphaValue == 1f },
        enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CommonNetworkImage(
                url = AppGlobalData.sLoginResult.profile.avatarUrl,
                placeholder = R.drawable.ic_default_avator,
                error = R.drawable.ic_default_avator,
                modifier = Modifier
                    .size(50.cdp)
                    .clip(
                        RoundedCornerShape(50)
                    )
            )
            Text(
                text = AppGlobalData.sLoginResult.profile.nickname,
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
    Image(
        painter = painterResource(id = R.drawable.ic_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxWidth()
            .height(584.cdp)
            .clip(BgImageShapes())
            .graphicsLayer {
                alpha = alphaValue
            }
    )
}


fun Modifier.mineCommonCard() = composed {
    this
        .fillMaxWidth()
        .padding(start = 32.cdp, end = 32.cdp, top = 20.cdp)
        .background(AppColorsProvider.current.card, RoundedCornerShape(24.cdp))
        .padding(top = 24.cdp, bottom = 24.cdp)
}


private val tabs = listOf("创建歌单", "收藏歌单", "歌单助手")



