package com.ssk.ncmusic.core.viewstate

/**
 * Created by ssk on 2022/4/29.
 */
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.ui.common.refresh.SwipeRefreshLayout
import com.ssk.ncmusic.ui.common.refresh.SwipeRefreshStateType
import com.ssk.ncmusic.ui.common.refresh.indicator.footer.CommonLoadFooter
import com.ssk.ncmusic.ui.common.refresh.rememberSwipeRefreshState
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp


/**
 * Created by ssk on 2022/4/3.
 */
/**
 * Created by ssk on 2022/1/11.
 * Description->通用列表组件，支持页面状态切换、下拉刷新、上拉加载更多
 * @param modifier：页面布局修饰
 * @param enableRefresh： 是否允许下拉刷新
 * @param showNoMoreDataFooter： 没有更多数据时，是否显示没有更多数据footer
 * @param specialRetryBlock：首次加载失败或者数据为空时，点击重试按钮执行的代码块，没设置的话，默认执行collectAsLazyPagingItems.refresh()
 * @param specialRefreshBlock：刷新代码块，没设置的话，默认执行collectAsLazyPagingItems.refresh()
 * @param collectAsLazyPagingItems：分页数据
 * @param lifeCycleListener：生命周期监听
 * @param customEmptyComponent：自定义空布局,没设置则使用默认空布局
 * @param customFailComponent：自定义失败布局,没设置则使用默认失败布局
 * @param listContent：正常页面内容
 */
@ExperimentalFoundationApi
@Composable
fun <T : Any> ViewStateListPagingComponent(
    modifier: Modifier,
    enableRefresh: Boolean = true,
    showNoMoreDataFooter: Boolean = true,
    collectAsLazyPagingItems: LazyPagingItems<T>,
    specialRetryBlock: (() -> Unit)? = null,
    specialRefreshBlock: (() -> Unit)? = null,
    lifeCycleListener: ComposeLifeCycleListener? = null,
    lazyListContentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
    viewStateComponentModifier: Modifier = Modifier.fillMaxSize(),
    viewStateContentAlignment: Alignment = Alignment.Center,
    customEmptyComponent: @Composable (() -> Unit)? = null,
    customFailComponent: @Composable (() -> Unit)? = null,
    listContent: LazyListScope.() -> Unit,
) {

    lifeCycleListener?.let { listener ->
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(Unit) {
            listener.onEnterCompose(lifecycleOwner)

            val lifecycleEventObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        listener.onCreate(lifecycleOwner)
                    }
                    Lifecycle.Event.ON_START -> {
                        listener.onStart(lifecycleOwner)
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        listener.onResume(lifecycleOwner)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        listener.onPause(lifecycleOwner)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        listener.onStop(lifecycleOwner)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        listener.onDestroy(lifecycleOwner)
                    }
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

            onDispose {
                listener.onExitCompose(lifecycleOwner)
                lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
            }
        }
    }

    var refreshStateType by remember {
        mutableStateOf<SwipeRefreshStateType>(SwipeRefreshStateType.IDLE)
    }

    val refreshState = rememberSwipeRefreshState(refreshStateType)


    // 首次进入改组件，数据还没加载成功，显示状态页面
    val showViewState = remember {
        mutableStateOf(true)
    }

    if (showViewState.value) {
        HandlerViewStateComponent(
            collectAsLazyPagingItems,
            showViewState,
            specialRetryBlock,
            viewStateContentAlignment,
            viewStateComponentModifier,
            customEmptyComponent,
            customFailComponent
        )
    } else {
        SwipeRefreshLayout(
            state = refreshState,
            swipeEnabled = enableRefresh,
            onRefresh = {
                specialRefreshBlock?.invoke() ?: collectAsLazyPagingItems.refresh()
            },
            onIdle = {
                refreshStateType = SwipeRefreshStateType.IDLE
            }
        ) {
            // 处理下拉刷新状态
            if (refreshState.isRefreshing()) {
                collectAsLazyPagingItems.apply {
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            Log.e("ssk", "下拉刷新异常")
                            refreshStateType = SwipeRefreshStateType.FAIL
                        }
                        is LoadState.NotLoading -> {
                            Log.e("ssk", "下拉刷新成功")
                            refreshStateType = SwipeRefreshStateType.SUCCESS
                        }
                        else -> {}
                    }
                }
            } else {
                if (collectAsLazyPagingItems.loadState.refresh is LoadState.Loading) {
                    Log.e("ssk", "开始下拉刷新")
                    refreshStateType = SwipeRefreshStateType.REFRESHING
                }
            }

            Log.d("ssk", "NewViewStateListPagingComponent inner recompose")
            LazyColumn(
                modifier = modifier,
                contentPadding = lazyListContentPadding,
                state = lazyListState
            ) {

                listContent()

                if (!refreshState.isRefreshing()) {
                    handleListPaging(
                        collectAsLazyPagingItems,
                        showNoMoreDataFooter
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : Any> HandlerViewStateComponent(
    collectAsLazyPagingItems: LazyPagingItems<T>,
    showViewState: MutableState<Boolean>,
    specialRetryBlock: (() -> Unit)? = null,
    viewStateContentAlignment: Alignment = Alignment.Center,
    viewStateComponentModifier: Modifier = Modifier.fillMaxSize(),
    customEmptyComponent: @Composable (() -> Unit)? = null,
    customFailComponent: @Composable (() -> Unit)? = null,
) {
    var hasShowLoadState by remember {
        mutableStateOf(false)
    }
    collectAsLazyPagingItems.apply {
        when (loadState.refresh) {
            is LoadState.Error -> {
                Log.e("ssk", "首次加载异常")
                // 首次加载异常
                val errorMessagePair = getErrorMessagePair((loadState.refresh as LoadState.Error).error)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = viewStateComponentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        customFailComponent?.invoke()
                            ?: NoSuccessComponent(message = errorMessagePair.first,
                                iconResId = errorMessagePair.second,
                                contentAlignment = viewStateContentAlignment,
                                specialRetryBlock = specialRetryBlock,
                                loadDataBlock = { collectAsLazyPagingItems.retry() })
                    }
                }
            }
            is LoadState.NotLoading -> {
                if (collectAsLazyPagingItems.itemCount == 0 && hasShowLoadState) {
                    Log.e("ssk", "首次加载数据为null")

                    // 首次加载数据为null
                    Column(modifier = viewStateComponentModifier) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            customEmptyComponent?.invoke()
                                ?: NoSuccessComponent(message = "暂无数据展示",
                                    iconResId = R.drawable.ic_empty,
                                    contentAlignment = viewStateContentAlignment,
                                    specialRetryBlock = specialRetryBlock,
                                    loadDataBlock = { collectAsLazyPagingItems.refresh() })
                        }
                    }
                } else if (collectAsLazyPagingItems.itemCount > 0) {
                    Log.e("ssk", "显示正常列表数据")

                    showViewState.value = false
                }
            }
            is LoadState.Loading -> {

                if (collectAsLazyPagingItems.itemCount <= 0) {
                    Log.e("ssk", "首次加载数据中")
                    // 首次加载数据中
                    Column(modifier = viewStateComponentModifier) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingComponent(contentAlignment = viewStateContentAlignment)
                        }
                        hasShowLoadState = true
                    }
                }
            }
        }
    }
}

private fun <T : Any> LazyListScope.handleListPaging(
    collectAsLazyPagingItems: LazyPagingItems<T>,
    showNoMoreDataFooter: Boolean = true,
) {
    collectAsLazyPagingItems.apply {
        when (loadState.append) {
            is LoadState.Loading -> {
                //加载更多，底部loading
                item {
                    Log.e("ssk", "加载更多，底部loading")
                    CommonLoadFooter()
                }
            }
            is LoadState.Error -> {
                //加载更多异常
                item {
                    Log.e("ssk", "加载更多异常")
                    LoadMoreDataErrorFooter {
                        collectAsLazyPagingItems.retry()
                    }
                }
            }
            LoadState.NotLoading(endOfPaginationReached = true) -> {
                if (collectAsLazyPagingItems.itemCount > 0 && showNoMoreDataFooter) {
                    Log.e("ssk", "已经没有更多数据了")
                    // 已经没有更多数据了
                    item {
                        NoMoreDataFooter()
                    }
                }
            }
            else -> {}
        }
    }
}

/**
 * 底部加载更多失败处理
 * */
@Composable
private fun LoadMoreDataErrorFooter(retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = "--加载失败,点击重试--",
            fontSize = 30.csp,
            color = AppColorsProvider.current.secondText,
            modifier = Modifier.clickable {
                retry.invoke()
            })
    }
}


/**
 * 没有更多数据footer
 */
@Composable
private fun NoMoreDataFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "--没有更多数据啦--",
            fontSize = 30.csp,
            color = AppColorsProvider.current.secondText
        )
    }
}


