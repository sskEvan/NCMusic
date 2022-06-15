package com.ssk.ncmusic.ui.page.comment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonTabLayout
import com.ssk.ncmusic.ui.common.CommonTabLayoutStyle
import com.ssk.ncmusic.ui.page.comment.component.CpnCommentPager
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.cloudcountry.VideoPlayViewModel
import com.ssk.ncmusic.viewmodel.comment.CommentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/25.
 */

var showVideoCommentSheet by mutableStateOf(false)
var videoCommentSheetOffset by mutableStateOf(ScreenUtil.getScreenHeight().toFloat())

@Composable
fun VideoCommentSheet() {
    if (showVideoCommentSheet) {
        VideoCommentSheetContent()
    } else {
        val viewModel: CommentViewModel = hiltViewModel()
        viewModel.title = "评论"
        viewModel.commentBeanListFlows.clear()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VideoCommentSheetContent() {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            scope.launch {
                delay(200)
                showVideoCommentSheet = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )

    LaunchedEffect(showVideoCommentSheet) {
        if (showVideoCommentSheet) {
            sheetState.show()
        }
    }

    BackHandler(showVideoCommentSheet) {
        scope.launch {
            sheetState.hide()
            showVideoCommentSheet = false
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            VideoCommentList(sheetState) {
                scope.launch {
                    sheetState.hide()
                    showVideoCommentSheet = false
                }
            }
        },
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent
    ) {
    }
}

@Composable
private fun VideoCommentList(sheetState: ModalBottomSheetState, onBack: () -> Unit) {
    if(sheetState.offset.value != 0f) {
        videoCommentSheetOffset = sheetState.offset.value
    }
    val videoPlayViewModel: VideoPlayViewModel = hiltViewModel()
    val commentViewModel: CommentViewModel = hiltViewModel()
    val height = (ScreenUtil.getScreenHeight() - 720).transformDp
    val pagerState = rememberPagerState(
        initialPage = 1,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(AppColorsProvider.current.background)
    ) {
        VideoCommentHeader(pagerState, onBack)
        videoPlayViewModel.curVideoId?.let {
            HorizontalPager(
                count = commentViewModel.commentSortTabs.size,
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false
            ) { position ->
                CpnCommentPager(it, commentViewModel.commentSortTabs[position].type, CommentViewModel.TYPE_VIDEO)
            }
        }
    }
}


@Composable
private fun VideoCommentHeader(pagerState: PagerState, onBack: () -> Unit) {
    val viewModel: CommentViewModel = hiltViewModel()
    var selectedIndex by remember { mutableStateOf(1) }
    val scopeState = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonIcon(
            resId = R.drawable.ic_video_comment_sheet_back,
            modifier = Modifier
                .size(44.cdp)
                .onClick(enableRipple = false) { onBack.invoke() },
            tint = AppColorsProvider.current.secondIcon
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.cdp)
                .padding(horizontal = 32.cdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = viewModel.title,
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
}
