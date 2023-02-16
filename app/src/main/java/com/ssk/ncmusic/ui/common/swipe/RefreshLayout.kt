package com.ssk.ncmusic.ui.common.swipe

import android.util.Log
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.zIndex
import com.ssk.ncmusic.ui.common.refresh.indicator.footer.CommonLoadMoreFooter
import com.ssk.ncmusic.ui.common.refresh.indicator.header.CommonRefreshHeader
import com.ssk.ncmusic.utils.transformDp
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/12/7.
 * 自定义下拉刷新、加载更多组件，配合paging3使用实现分页
 */


@Composable
fun RefreshLayout(
    modifier: Modifier = Modifier,
    enableRefresh: Boolean = true,
    enableLoadMore: Boolean = true,
    refreshState: RefreshState,
    scrollState: ScrollableState? = null,
    onRefresh: (() -> Unit)? = null,
    loadMoreRetryBlock: () -> Unit,
    headerIndicator: @Composable () -> Unit = { CommonRefreshHeader(refreshState.type) },
    footerIndicator: @Composable () -> Unit = { CommonLoadMoreFooter(refreshState, loadMoreRetryBlock) },
    content: @Composable () -> Unit,
) {

    if (!enableRefresh && !enableLoadMore) {
        content()
    } else {
        val animScope = rememberCoroutineScope()
        val scrollStateScope = rememberCoroutineScope()

        SubComposeSmartSwipeRefresh(
            headerIndicator = headerIndicator,
            footerIndicator = footerIndicator,
            enableRefresh,
            enableLoadMore,
        ) { headerHeight, footerHeight ->

            val scrollConnection = remember {
                RefreshNestedScrollConnection(
                    refreshState,
                    enableRefresh,
                    enableLoadMore,
                    animScope,
                    headerHeight,
                    footerHeight,
                    onRefresh,
                )
            }

            LaunchedEffect(refreshState.type) {
                if (refreshState.type == RefreshType.REFRESH_SUCCESS
                    || refreshState.type == RefreshType.REFRESH_FAIL
                ) {
                    Log.e("ssk", "LaunchedEffect 刷新成功或者失败")
                    scrollConnection.animateToOffset(RefreshNestedScrollConnection.ACTION_REFRESH, 0f, headerHeight, 500L)
                }
                if (refreshState.type == RefreshType.LOAD_MORE_SUCCESS
                    || refreshState.type == RefreshType.LOAD_MORE_FAIL
                ) {
                    Log.e("ssk", "LaunchedEffect 加载更多成功或者失败")
                    if (refreshState.type == RefreshType.LOAD_MORE_SUCCESS) {
                        scrollStateScope.launch {
                            scrollState?.animateScrollBy(footerHeight)
                        }
                    }
                    scrollConnection.animateToOffset(RefreshNestedScrollConnection.ACTION_LOAD_MORE, 0f, headerHeight, 500L)
                }
            }

            LaunchedEffect(refreshState.noMoreData) {
                if (refreshState.noMoreData) {
                    scrollConnection.animateToOffset(RefreshNestedScrollConnection.ACTION_LOAD_MORE, 0f, headerHeight, 500L)
                }
            }

            val curOffset = scrollConnection.offset
            Box(
                modifier = modifier
                    .nestedScroll(scrollConnection)
            ) {
                if (enableRefresh) {
                    Box(Modifier.offset(y = (-headerHeight + curOffset).transformDp)) {
                        headerIndicator()
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(y = curOffset.transformDp)
                ) {
                    content()
                }

                if (enableLoadMore) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (footerHeight + curOffset).transformDp)
                    ) {
                        footerIndicator()
                    }
                }
            }
        }
    }
}


@Composable
private fun SubComposeSmartSwipeRefresh(
    headerIndicator: @Composable () -> Unit,
    footerIndicator: @Composable () -> Unit,
    enableRefresh: Boolean,
    enableLoadMore: Boolean,
    content: @Composable (headerHeight: Float, footerHeight: Float) -> Unit
) {
    SubcomposeLayout(
        modifier = Modifier
            .zIndex(-1f)
            .clipToBounds()
    ) { constraints: Constraints ->
        val headerIndicatorPlaceable = subcompose("headerIndicator", headerIndicator).first().measure(constraints)
        val footerIndicatorPlaceable = subcompose("footerIndicator", footerIndicator).first().measure(constraints)
        val contentPlaceable = subcompose("content") {
            content(
                if (enableRefresh) headerIndicatorPlaceable.height.toFloat() else 0f,
                if (enableLoadMore) footerIndicatorPlaceable.height.toFloat() else 0f
            )
        }.map {
            it.measure(constraints)
        }.first()
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}
