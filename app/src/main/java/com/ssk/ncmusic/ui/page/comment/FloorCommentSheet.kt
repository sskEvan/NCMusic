package com.ssk.ncmusic.ui.page.comment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.itemsIndexed
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.page.comment.component.CpnCommentItem
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.viewmodel.comment.FloorCommentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/4.
 */


@Composable
fun FloorCommentSheet(type: Int) {
    if (FloorCommentViewModel.showFloorCommentSheet) {
        FloorCommentSheetContent(type)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FloorCommentSheetContent(type: Int) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            scope.launch {
                delay(200)
                FloorCommentViewModel.showFloorCommentSheet = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )

    LaunchedEffect(FloorCommentViewModel.showFloorCommentSheet) {
        if (FloorCommentViewModel.showFloorCommentSheet) {
            sheetState.show()
        }
    }

    BackHandler(FloorCommentViewModel.showFloorCommentSheet) {
        scope.launch {
            sheetState.hide()
            FloorCommentViewModel.showFloorCommentSheet = false
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            FloorCommentList(type) {
                scope.launch {
                    sheetState.hide()
                    FloorCommentViewModel.showFloorCommentSheet = false
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
private fun FloorCommentList(type: Int, onBack: () -> Unit) {
    val key = "FloorCommentViewModel-${FloorCommentViewModel.floorOwnerCommentId}"
    val viewModel: FloorCommentViewModel = hiltViewModel(key = key)
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

        ViewStateListPagingComponent(
            key = "FloorCommentList-${FloorCommentViewModel.floorOwnerCommentId}",
            loadDataBlock = { viewModel.getFloorCommentResult(type) },
            viewStateContentAlignment = BiasAlignment(0f, -0.6f),
            enableRefresh = false
        ) { commentBeanList ->

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
