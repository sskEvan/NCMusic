package com.ssk.ncmusic.ui.page.comment.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.itemsIndexed
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.viewmodel.comment.CommentViewModel
import com.ssk.ncmusic.viewmodel.comment.FloorCommentViewModel

/**
 * Created by ssk on 2022/5/25.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CpnCommentPager(id: String, sortType: Int, type: Int) {
    val viewModel: CommentViewModel = hiltViewModel()

    ViewStateListPagingComponent(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        key = "CpnCommentPager-${sortType}-${type}",
        loadDataBlock = { viewModel.buildNewCommentListPager(id, sortType, type) },
        viewStateComponentModifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        viewStateContentAlignment = BiasAlignment(0f, -0.6f),
        enableRefresh = false
    ) { commentBeanList ->
        Log.e("ssk", "CommentPager recompose")
        itemsIndexed(commentBeanList) { _, data ->
            data?.let {
                CpnCommentItem(data) { commentBean ->
                    FloorCommentViewModel.floorOwnerCommentId = commentBean.commentId
                    FloorCommentViewModel.showFloorCommentSheet = true
                }
            }
        }
    }
}





