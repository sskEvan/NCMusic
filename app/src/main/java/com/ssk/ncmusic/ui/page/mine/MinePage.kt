package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.api.NCApi
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.model.UserPlaylistResult
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.toPx
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/17.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinePage() {
    val viewModel: MineViewModel = hiltViewModel()
    var bodyAlphaValue by remember { mutableStateOf(1f) }
    val topBarAlphaValue = remember { mutableStateOf(0f) }
    val sysUiController = rememberSystemUiController()
    //sysUiController.setSystemBarsColor(Color.White.copy(alpha = topBarAlphaValue.value), darkIcons = true)
    sysUiController.setSystemBarsColor(Color.Transparent, darkIcons = true)

    Box(modifier = Modifier.fillMaxSize()) {
        ViewStateComponent(viewStateLiveData = viewModel.userPlaylistResult,
            loadDataBlock = { viewModel.getUserPlayList() }) {

            Box {
                var dragStatus by remember {
                    mutableStateOf<DragStatus>(DragStatus.Idle)
                }
                val dragToggleState = rememberDragToggleState(dragStatus)
                FixHeadBackgroundDraggableBodyLayout(
                    state = dragToggleState,
                    triggerRadio = 0.32f,
                    maxDragRadio = 0.54f,
                    modifier = Modifier.background(Color(0xFFEEEEEE)),
                    onOverOpenTrigger = {
                        dragStatus = DragStatus.OverOpenTrigger
                    },
                    onOpened = {
                        dragStatus = DragStatus.Opened
                        //dragStatus = DragStatus.Idle
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
                    Body(dragToggleState, 1 - bodyAlphaValue, topBarAlphaValue)
                }
            }
        }

        TopBar(topBarAlphaValue.value)
    }
}

@Composable
private fun TopBar(alphaValue: Float) {
    CommonTopAppBar(
        modifier = Modifier.background(Color.White.copy(alpha = alphaValue)).statusBarsPadding(),
        backgroundColor = Color.Transparent,
        leftIconResId = R.drawable.ic_drawer_toggle,
        leftClick = { },
        rightIconResId = R.drawable.ic_search
    )
}

@Composable
private fun HeaderBackground(alphaValue: Float) {
    Image(
        painter = painterResource(id = R.drawable.ic_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(BgImageShapes())
            .graphicsLayer {
                alpha = alphaValue
            }
    )
}

private var animateScrolling = false

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(dragToggleState: DragToggleState, bodyAlphaValue: Float, topBarAlphaValue: MutableState<Float>) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { -88.cdp.toPx.toInt() }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val viewModel: MineViewModel = hiltViewModel()

    if (dragToggleState.isDraggableInProgress) {
        animateScrolling = false
    }

    if(scrollState.firstVisibleItemIndex == 0) {
        var topBarAlpha = scrollState.firstVisibleItemScrollOffset / 240f
        if(topBarAlpha > 1) topBarAlpha = 1f
        topBarAlphaValue.value = topBarAlpha
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(if (bodyAlphaValue == 1f) AppColorsProvider.current.background else Color.Transparent),
        state = scrollState,
    ) {
        item {
            UserInfoComponent(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 88.cdp)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = bodyAlphaValue }
                    .mineCommonCard()
                    .height(300.cdp),
                contentAlignment = Alignment.Center
            ) {
                MusicApplicationComponent()
            }
        }

        item {
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = bodyAlphaValue }
                    .mineCommonCard(),
                contentAlignment = Alignment.Center
            ) {
                UserPlaylistItem(viewModel.favoritePlayList)
            }
        }

        stickyHeader {
            CommonTabLayout(
                tabTexts = tabs,
                style = CommonTabLayoutStyle(isScrollable = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.cdp)
                        .graphicsLayer { alpha = bodyAlphaValue }),
                selectedIndex = selectedTabIndex.value
            ) {
                Log.d("ssk", "CommonTabLayout selectedTabIndex=${it}")
                selectedTabIndex.value = it
                animateScrolling = true
                coroutineScope.launch {
                    Log.d("ssk", "CommonTabLayout start animateScrolling animateScrolling=${animateScrolling}")
                    scrollState.animateScrollToItem(it + 4, offsetY)
                    animateScrolling = false
                    Log.d("ssk", "CommonTabLayout end animateScrolling animateScrolling=${animateScrolling}")
                }
            }
        }

        item {
            UserPlaylistComponent(
                modifier = Modifier.graphicsLayer {
                    alpha = bodyAlphaValue
                },
                list = viewModel.selfCreatePlayList,
                title = "创建歌单",
                itemPosition = 5,
                selectedTabIndex = selectedTabIndex
            )
        }

        item {
            UserPlaylistComponent(
                modifier = Modifier.graphicsLayer { alpha = bodyAlphaValue },
                list = viewModel.collectPlayList,
                title = "收藏歌单",
                itemPosition = 6,
                selectedTabIndex = selectedTabIndex
            )
        }

        item {
            Box(
                modifier = Modifier
                    .padding(bottom = 30.cdp)
                    .mineCommonCard()
                    .height(500.cdp)
                    .onGloballyPositioned {
                        if (!animateScrolling) {
                            val lastVisibleItem = scrollState.layoutInfo.visibleItemsInfo[scrollState.layoutInfo.visibleItemsInfo.size - 1]
                            if (lastVisibleItem.offset + lastVisibleItem.size == scrollState.layoutInfo.viewportSize.height) {  // 滑动到底部
                                selectedTabIndex.value = 2
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("歌单助手")
            }
        }
    }
}

@Composable
private fun UserPlaylistComponent(
    modifier: Modifier = Modifier,
    list: List<PlaylistBean>?,
    title: String,
    itemPosition: Int,
    selectedTabIndex: MutableState<Int>,
) {
    val stickyHeight = remember {
        88.cdp.toPx.toInt() + 12.cdp.toPx.toInt()
    }

    list?.let {
        Box(
            modifier = modifier
                .mineCommonCard()
                .onGloballyPositioned {
                    if (!animateScrolling) {
                        val top = it.boundsInParent().top
                        val bottom = it.boundsInParent().bottom
                        if (top <= stickyHeight && bottom > stickyHeight) {
                            selectedTabIndex.value = itemPosition - 5
                            Log.e("ssk", "222 scrollAboveTabLayoutWatcher=${selectedTabIndex.value},animateScrolling=${animateScrolling}")
                        }
                    }
                }
        ) {
            Column {
                Text(
                    text = "${title}(${list.size}个)",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 6.dp, top = 12.cdp)
                )
                it.forEach {
                    UserPlaylistItem(it)
                }
            }
        }
    }
}


@Composable
fun Modifier.mineCommonCard() = this
    .fillMaxWidth()
    //.shadow(4.cdp, RoundedCornerShape(24.cdp))
    .padding(start = 32.cdp, end = 32.cdp, top = 20.cdp)
    .background(AppColorsProvider.current.card, RoundedCornerShape(24.cdp))
    .padding(start = 32.cdp, end = 32.cdp, top = 24.cdp, bottom = 24.cdp)


private val tabs = listOf("创建歌单", "收藏歌单", "歌单助手")

@HiltViewModel
class MineViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    var favoritePlayList: PlaylistBean? by mutableStateOf(null)
    var selfCreatePlayList: List<PlaylistBean>? by mutableStateOf(null)
    var collectPlayList: List<PlaylistBean>? by mutableStateOf(null)



    val userPlaylistResult = ViewStateMutableLiveData<UserPlaylistResult>()

    fun getUserPlayList() {
        launch(userPlaylistResult, handleResult = {
            val selfCreateList = mutableListOf<PlaylistBean>()
            val collectList = mutableListOf<PlaylistBean>()

            it.playlist.forEach {
                if (it.creator.userId == AppGlobalData.sLoginResult.account.id) {
                    if (it.name.equals(it.creator.nickname + "喜欢的音乐")) {
                        favoritePlayList = it
                    } else {
                        selfCreateList.add(it)
                    }
                } else {
                    collectList.add(it)
                }

            }
            selfCreatePlayList = selfCreateList
            collectPlayList = collectList
        }) {
            //api.getUserPlayList(AppGlobalData.sLoginResult.account.id.toString())

            val gson = Gson()
            gson.fromJson(MockData.playList, UserPlaylistResult::class.java)
        }
    }
}

