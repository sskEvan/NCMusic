package com.ssk.ncmusic.ui.page.comment.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.ui.page.comment.showFloorCommentSheet
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.viewmodel.comment.CommentViewModel

/**
 * Created by ssk on 2022/5/25.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun CpnCommentPager(id: String, sortType: Int, type: Int) {
    val viewModel: CommentViewModel = hiltViewModel()
    if (viewModel.commentBeanListFlows[sortType] == null) {
        viewModel.buildNewCommentListPager(id, sortType, type)
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
                        viewModel.floorOwnerCommentId = commentBean.commentId
                        showFloorCommentSheet = true
                    }
                }
            }
        }
    }
}




