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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonHeadBackgroundShape
import com.ssk.ncmusic.ui.common.CommonLocalImage
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.ui.theme.isInDarkTheme
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.mine.SongCommentViewModel
import me.onebone.toolbar.*

/**
 * Created by ssk on 2022/4/28.
 */
@Composable
fun SongCommentPage(songBean: SongBean) {
    BackHandler(true) {
        NCNavController.instance.popBackStack()
        MusicPlayController.playMusicSheetOffset = 0
    }

    val sysUiController = rememberSystemUiController()
    sysUiController.setSystemBarsColor(Color.Transparent, darkIcons = !isInDarkTheme())
    Log.e("ssk", "SongCommentPage recompose !!!!")

    val state = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            ScrollHeader(songBean, state.toolbarState)
        }
    ) {
        Body(songBean)
    }
//    Column(modifier = Modifier.background(AppColorsProvider.current.background)) {
//        CommonTopAppBar(
//            modifier = Modifier.statusBarsPadding(),
//            title = "评论",
//            titleAlign = TextAlign.Start,
//            leftClick = {
//                NCNavController.instance.popBackStack()
//                MusicPlayController.playMusicSheetOffset = 0
//            })
//        Body(songBean)
//    }
}

@Composable
private fun CollapsingToolbarScope.ScrollHeader(songBean: SongBean, toolbarState: CollapsingToolbarState) {
    val maxHeight = LocalWindowInsets.current.statusBars.top.transformDp + (88 + 150 + 100 + 20).cdp
    Log.e("ssk", "progress=${toolbarState.progress}")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        SongInfoComponent(songBean)
        StickyHeader()
    }

    Column {
        Box(Modifier.fillMaxWidth().statusBarsHeight().background(AppColorsProvider.current.background))
        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            title = "评论",
            titleAlign = TextAlign.Left,
            leftClick = {
                NCNavController.instance.popBackStack()
                MusicPlayController.playMusicSheetOffset = 0
            },
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(songBean: SongBean) {
    Log.e("ssk", "SongCommentPage  body recompose !!!!")
    val viewModel: SongCommentViewModel = viewModel()

    LaunchedEffect(viewModel.curSelectedTabType) {
        viewModel.buildNewCommentListPager(songBean, viewModel.curSelectedTabType)
    }
    if (viewModel.commentBeanListFlows[viewModel.curSelectedTabType] != null) {
        val commentBeanList = viewModel.commentBeanListFlows[viewModel.curSelectedTabType]!!.collectAsLazyPagingItems()
        ViewStateListPagingComponent(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColorsProvider.current.background),
            viewStateComponentModifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background),
            enableRefresh = false,
            collectAsLazyPagingItems = commentBeanList,
        ) {
            itemsIndexed(commentBeanList) { index, data ->
                data?.let {
                    Text(
                        text = "${it.content}", modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.cdp)
                    )
                }
            }
        }
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

@Composable
private fun StickyHeader() {
    val viewModel: SongCommentViewModel = viewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp)
            //.background(AppColorsProvider.current.background)
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            viewModel.commentSortTabs.forEachIndexed { index, item ->
                Text(
                    modifier = Modifier
                        .width(100.cdp)
                        .onClick(enableRipple = false) {
                            viewModel.curSelectedTabType = item.type
                        },
                    textAlign = TextAlign.Center,
                    text = item.title,
                    color = if (item.type == viewModel.curSelectedTabType) {
                        AppColorsProvider.current.firstText
                    } else {
                        AppColorsProvider.current.secondText
                    },
                    fontSize = 28.csp,
                    fontWeight = if (item.type == viewModel.curSelectedTabType) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
                if (index != viewModel.commentSortTabs.size - 1) {
                    Divider(
                        modifier = Modifier.width(2.cdp),
                        thickness = 30.cdp,
                        color = AppColorsProvider.current.divider
                    )
                }
            }
        }
    }
}
