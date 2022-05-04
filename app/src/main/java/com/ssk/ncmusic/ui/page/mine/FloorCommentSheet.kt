package com.ssk.ncmusic.ui.page.mine

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.ui.page.mine.component.CpnCommentItem
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.viewmodel.mine.SongCommentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/4.
 */
@Composable
fun FloorCommentSheet() {
    val viewModel: SongCommentViewModel = hiltViewModel()
    if (viewModel.showFloorCommentSheet) {
        FloorCommentSheetContent()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FloorCommentSheetContent() {
    val viewModel: SongCommentViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            scope.launch {
                delay(200)
                viewModel.showFloorCommentSheet = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )

    LaunchedEffect(viewModel.showFloorCommentSheet) {
        if (viewModel.showFloorCommentSheet) {
            sheetState.show()
        }
    }

    BackHandler(viewModel.showFloorCommentSheet) {
        scope.launch {
            sheetState.hide()
            viewModel.showFloorCommentSheet = false
        }
    }

    ModalBottomSheetLayout(
        sheetContent = { FloorCommentList() },
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent
    ) {
    }
}

@Composable
private fun FloorCommentList() {
    val viewModel: SongCommentViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .clip(RoundedCornerShape(topStart = 40.cdp, topEnd = 40.cdp))
            .background(AppColorsProvider.current.pure)
            .padding(top = 48.cdp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 48.cdp)
                .fillMaxWidth()
                .height(80.cdp)
        ) {
            Text(
                text = "回复",
                color = AppColorsProvider.current.firstText,
                fontSize = 36.csp,
                fontWeight = FontWeight.Medium
            )
        }

        LaunchedEffect(viewModel.floorOwnerCommentId) {
            viewModel.getFloorCommentResult(
                viewModel.floorOwnerCommentId,
                viewModel.songBean?.id ?: 0L
            )
        }

        ViewStateComponent(
            viewStateLiveData = viewModel.floorCommentResult,
            specialRetryBlock = {
                viewModel.getFloorCommentResult(
                    viewModel.floorOwnerCommentId,
                    viewModel.songBean?.id ?: 0L
                )
            }) { data ->
            LazyColumn {
                data.data.ownerComment.let { ownerComment ->
                    item {
                        Column {
                            CpnCommentItem(comment = ownerComment, isFloorComment = true)
                            Divider(
                                color = AppColorsProvider.current.divider.copy(0.6f),
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 20.cdp
                            )
                        }
                    }
                }

                items(data.data.comments) {
                    CpnCommentItem(comment = it, isFloorComment = true)
                }
            }
        }
    }
}