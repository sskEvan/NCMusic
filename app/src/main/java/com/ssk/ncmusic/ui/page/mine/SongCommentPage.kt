package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.page.mine.component.CpnCommentItem
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.transformDp
import com.ssk.ncmusic.viewmodel.mine.SongCommentViewModel
import kotlinx.coroutines.launch
import me.onebone.toolbar.*

/**
 * Created by ssk on 2022/4/28.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SongCommentPage(songBean: SongBean) {

    Log.e("ssk", "SongCommentPage recompose !!!!")
    val viewModel: SongCommentViewModel = hiltViewModel()

    BackHandler(true) {
        NCNavController.instance.popBackStack()
        MusicPlayController.playMusicSheetOffset = 0
    }

    Box {
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = viewModel.commentSortTabs.size,
        )

        val state = rememberCollapsingToolbarScaffoldState()
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background),
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                ScrollHeader(songBean, pagerState)
            }
        ) {
            Body(songBean, pagerState)
        }

        FloorCommentSheet()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CollapsingToolbarScope.ScrollHeader(songBean: SongBean, pagerState: PagerState) {
    val viewModel: SongCommentViewModel = hiltViewModel()
    val maxHeight = LocalWindowInsets.current.statusBars.top.transformDp + (88 + 150 + 100 + 20).cdp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        SongInfoComponent(songBean)
        StickyHeader(pagerState)
    }

    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(AppColorsProvider.current.background)
        )

        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            title = viewModel.title,
            titleAlign = TextAlign.Left,
            leftClick = {
                NCNavController.instance.popBackStack()
                MusicPlayController.playMusicSheetOffset = 0
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.cdp)
        )
    }
}

@Composable
private fun SongInfoComponent(songBean: SongBean) {
    Column {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 88.cdp)
                .fillMaxWidth()
                .height(150.cdp)
                .padding(horizontal = 42.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(104.cdp),
                contentAlignment = Alignment.Center
            ) {
                CommonLocalImage(
                    R.drawable.ic_disc,
                    modifier = Modifier.fillMaxSize()
                )
                CommonNetworkImage(
                    songBean.al.picUrl,
                    placeholder = R.drawable.ic_default_disk_cover,
                    error = R.drawable.ic_default_disk_cover,
                    modifier = Modifier
                        .size(70.cdp)
                        .clip(CircleShape)
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.firstText, fontSize = 36.csp)) {
                        append(songBean.name)
                    }
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.secondText, fontSize = 32.csp)) {
                        append(" - ${songBean.ar[0].name}")
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 22.cdp, end = 48.cdp)
            )
        }

        Divider(
            color = AppColorsProvider.current.divider,
            modifier = Modifier.fillMaxWidth(),
            thickness = 20.cdp
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StickyHeader(pagerState: PagerState) {
    val viewModel: SongCommentViewModel = hiltViewModel()
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    val scopeState = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp)
            .padding(horizontal = 32.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "评论区",
            color = AppColorsProvider.current.firstText,
            fontSize = 32.csp,
            fontWeight = FontWeight.Bold
        )

        CommonTabLayout(
            tabTexts = viewModel.commentSortTabs.map { it.title },
            backgroundColor = Color.Transparent,
            style = CommonTabLayoutStyle(isScrollable = false,
                modifier = Modifier.width(300.cdp),
                selectedTextSize = 28.csp,
                unselectedTextSize = 28.csp,
                tabItemDrawBehindBlock = { position ->
                    if (position != viewModel.commentSortTabs.size - 1) {
                        drawLine(
                            Color.LightGray,
                            Offset(size.width, size.height * 0.35f),
                            Offset(size.width, size.height * 0.65f),
                            strokeWidth = 2.cdp.toPx()
                        )
                    }
                },
                customIndicator = { _, _ -> }
            ),
            selectedIndex = selectedIndex
        ) {
            Log.e("ssk2", "viewModel.selectedTabIndex=${it}")
            selectedIndex = it
            scopeState.launch {
                pagerState.scrollToPage(selectedIndex)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
private fun Body(songBean: SongBean, pagerState: PagerState) {
    val viewModel: SongCommentViewModel = hiltViewModel()
    Log.e("ssk2", "SongCommentPage  body recompose !!!!viewModel=${viewModel.hashCode()}")


    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        dragEnabled = false
    ) { position ->
        CommentPager(songBean, viewModel.commentSortTabs[position].type)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
private fun CommentPager(songBean: SongBean, sortType: Int) {
    val viewModel: SongCommentViewModel = hiltViewModel()
    if (viewModel.commentBeanListFlows[sortType] == null) {
        viewModel.buildNewCommentListPager(sortType, songBean)
    }
    viewModel.commentBeanListFlows[sortType]?.let {
        val commentBeanList = it.collectAsLazyPagingItems()
        ViewStateListPagingComponent(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background),
            viewStateComponentModifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background),
            collectAsLazyPagingItems = commentBeanList,
            viewStateContentAlignment = BiasAlignment(0f, -0.6f),
            enableRefresh = false
        ) {
            Log.e("ssk", "CommentPager recompose")
            itemsIndexed(commentBeanList) { _, data ->
                data?.let {
                    CpnCommentItem(data) { commentBean ->
                        viewModel.songBean = songBean
                        viewModel.floorOwnerCommentId = commentBean.commentId
                        viewModel.showFloorCommentSheet = true
                    }
                }
            }
        }
    }
}




