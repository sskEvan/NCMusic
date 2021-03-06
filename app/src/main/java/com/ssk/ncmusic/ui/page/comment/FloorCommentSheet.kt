package com.ssk.ncmusic.ui.page.comment

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.page.comment.component.CpnCommentItem
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.viewmodel.comment.CommentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/4.
 */

var showFloorCommentSheet by mutableStateOf(false)

@Composable
fun FloorCommentSheet() {
    if (showFloorCommentSheet) {
        FloorCommentSheetContent()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FloorCommentSheetContent() {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            scope.launch {
                delay(200)
                showFloorCommentSheet = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )

    LaunchedEffect(showFloorCommentSheet) {
        if (showFloorCommentSheet) {
            sheetState.show()
        }
    }

    BackHandler(showFloorCommentSheet) {
        scope.launch {
            sheetState.hide()
            showFloorCommentSheet = false
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            FloorCommentList {
                scope.launch {
                    sheetState.hide()
                    showFloorCommentSheet = false
                }
            }
        },
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent
    ) {
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FloorCommentList(onBack: () -> Unit) {
    val viewModel: CommentViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .clip(RoundedCornerShape(topStart = 40.cdp, topEnd = 40.cdp))
            .background(AppColorsProvider.current.background)
            .padding(top = 16.cdp)
    ) {

        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            title = viewModel.floorCommentTitle,
            titleAlign = TextAlign.Left,
            backgroundColor = Color.Transparent,
            leftClick = {
                onBack()
            },
        )

        LaunchedEffect(viewModel.floorOwnerCommentId) {
            viewModel.getFloorCommentResult(
                viewModel.floorOwnerCommentId,
                CommentViewModel.TYPE_SONG
            )
        }
        viewModel.floorCommentBeanListFlow?.let {
            val commentBeanList = it.collectAsLazyPagingItems()
            ViewStateListPagingComponent(
                collectAsLazyPagingItems = commentBeanList,
                viewStateContentAlignment = BiasAlignment(0f, -0.6f),
                enableRefresh = false
            ) {

                viewModel.floorOwnerCommentBean?.let {
                    item {
                        Column {
                            CpnCommentItem(comment = it, isFloorComment = true)
                            Divider(
                                color = AppColorsProvider.current.divider.copy(0.6f),
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 20.cdp
                            )
                        }
                    }
                }

                itemsIndexed(commentBeanList) { _, data ->
                    data?.let { commentBean ->
                        CpnCommentItem(comment = commentBean, isFloorComment = true)
                    }
                }

            }
        }
    }
}
