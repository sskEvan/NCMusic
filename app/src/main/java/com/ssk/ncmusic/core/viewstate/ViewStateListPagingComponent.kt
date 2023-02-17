package com.ssk.ncmusic.core.viewstate

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.ui.common.swipe.RefreshLayout
import com.ssk.ncmusic.ui.common.swipe.RefreshState
import com.ssk.ncmusic.ui.common.swipe.RefreshType
import kotlinx.coroutines.flow.Flow


/**
 * Created by ssk on 2022/4/3.
 */
/**
 * Description->通用列表组件，支持页面状态切换、下拉刷新、上拉加载更多
 * @param modifier：页面布局修饰
 * @param key：配合PagingStateHolderViewModel使用，用于创建唯一的PagingStateHolderViewModel实例
 * @param enableRefresh： 是否允许下拉刷新
 * @param loadDataBlock：数据拉取接口
 * @param specialRetryBlock：首次加载失败或者数据为空时，点击重试按钮执行的代码块，没设置的话，默认执行collectAsLazyPagingItems.refresh()
 * @param specialRefreshBlock：刷新代码块，没设置的话，默认执行collectAsLazyPagingItems.refresh()
 * @param lifeCycleListener：生命周期监听
 * @param viewStateComponentModifier：页面状态组件Modifier
 * @param viewStateContentAlignment： 页面状态内容摆放模式
 * @param customEmptyComponent：自定义空布局,没设置则使用默认空布局
 * @param customFailComponent：自定义失败布局,没设置则使用默认失败布局
 * @param listContent：正常页面内容
 */
@ExperimentalFoundationApi
@Composable
fun <T : Any> ViewStateListPagingComponent(
    modifier: Modifier = Modifier,
    key: String = "",
    enableRefresh: Boolean = true,
    loadDataBlock: () -> Flow<PagingData<T>>,
    specialRetryBlock: (() -> Unit)? = null,
    specialRefreshBlock: (() -> Unit)? = null,
    lifeCycleListener: ComposeLifeCycleListener? = null,
    lazyListContentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
    viewStateComponentModifier: Modifier = Modifier.fillMaxSize(),
    viewStateContentAlignment: Alignment = Alignment.Center,
    customEmptyComponent: @Composable (() -> Unit)? = null,
    customFailComponent: @Composable (() -> Unit)? = null,
    listContent: LazyListScope.(collectAsLazyPagingItems: LazyPagingItems<T>) -> Unit,
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

    val pagingStateHolder: PagingStateHolderViewModel<T> = hiltViewModel(key = key)
    val collectAsLazyPagingItems = pagingStateHolder.getPagingDataFlow(loadDataBlock).collectAsLazyPagingItems()
    val refreshState = pagingStateHolder.refreshState

    if (pagingStateHolder.showViewState.value) {
        HandlerViewStateComponent(
            viewStateComponentModifier,
            key,
            collectAsLazyPagingItems,
            pagingStateHolder,
            specialRetryBlock,
            viewStateContentAlignment,
            customEmptyComponent,
            customFailComponent
        )
    } else {
        RefreshLayout(
            enableRefresh = enableRefresh,
            refreshState = refreshState,
            scrollState = lazyListState,
            onRefresh = {
                if (specialRefreshBlock != null) {
                    specialRefreshBlock.invoke()
                } else {
                    collectAsLazyPagingItems.refresh()
                }
            },
            loadMoreRetryBlock = {
                collectAsLazyPagingItems.retry()
            },
        ) {
            // 处理下拉刷新状态
            if (refreshState.type == RefreshType.REFRESHING) {
                collectAsLazyPagingItems.apply {
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            Log.e("ssk", "---------下拉刷新异常, state = $refreshState")
                            refreshState.finishRefresh(false)
                        }
                        is LoadState.NotLoading -> {
                            refreshState.finishRefresh(true)
                            Log.e("ssk", "-----------下拉刷新成功, state = $refreshState")
                        }
                        else -> {}
                    }
                }
            } else {
                if (collectAsLazyPagingItems.loadState.refresh is LoadState.Loading) {
                    Log.e("ssk", "开始下拉刷新")
                    refreshState.type = RefreshType.REFRESHING
                }
            }
            CompositionLocalProvider(LocalOverscrollConfiguration.provides(null)) {
                LazyColumn(
                    modifier = modifier,
                    contentPadding = lazyListContentPadding,
                    state = lazyListState
                ) {
                    listContent(collectAsLazyPagingItems)
                    handleListFooter(key, refreshState, collectAsLazyPagingItems)
                }
            }
        }
    }
}

@Composable
private fun <T : Any> HandlerViewStateComponent(
    viewStateComponentModifier: Modifier = Modifier.fillMaxSize(),
    key: String,
    collectAsLazyPagingItems: LazyPagingItems<T>,
    pagingStateHolder: PagingStateHolderViewModel<T>,
    specialRetryBlock: (() -> Unit)? = null,
    viewStateContentAlignment: Alignment = Alignment.Center,
    customEmptyComponent: @Composable (() -> Unit)? = null,
    customFailComponent: @Composable (() -> Unit)? = null,
) {

    collectAsLazyPagingItems.apply {
        when (loadState.refresh) {
            is LoadState.Error -> {
                Log.e("ssk", "首次加载异常,key=$key")
                // 首次加载异常
                val errorMessagePair = getErrorMessagePair((loadState.refresh as LoadState.Error).error)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = viewStateComponentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        if (customFailComponent != null) {
                            customFailComponent.invoke()
                        } else {
                            NoSuccessComponent(message = errorMessagePair.first,
                                iconResId = errorMessagePair.second,
                                contentAlignment = viewStateContentAlignment,
                                specialRetryBlock = specialRetryBlock,
                                loadDataBlock = { collectAsLazyPagingItems.retry() })
                        }
                    }
                }
            }
            is LoadState.NotLoading -> {
                if (collectAsLazyPagingItems.itemCount == 0 && pagingStateHolder.hasLoadingDone.value) {
                    Log.e("ssk", "首次加载数据为null,key=$key")

                    // 首次加载数据为null
                    Column(modifier = viewStateComponentModifier) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customEmptyComponent != null) {
                                customEmptyComponent.invoke()
                            } else {
                                NoSuccessComponent(message = "暂无数据展示",
                                    iconResId = R.drawable.ic_empty,
                                    contentAlignment = viewStateContentAlignment,
                                    specialRetryBlock = specialRetryBlock,
                                    loadDataBlock = { collectAsLazyPagingItems.refresh() })
                            }
                        }
                    }
                } else if (collectAsLazyPagingItems.itemCount > 0) {
                    Log.e("ssk", "显示正常列表数据,key=$key")
                    pagingStateHolder.showViewState.value = false
                }
            }
            is LoadState.Loading -> {

                if (collectAsLazyPagingItems.itemCount <= 0) {
                    Log.e("ssk", "首次加载数据中,key=$key")
                    // 首次加载数据中
                    Column(modifier = viewStateComponentModifier) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingComponent(contentAlignment = viewStateContentAlignment)
                        }
                        pagingStateHolder.hasLoadingDone.value = true
                    }
                }
            }
        }
    }
}

private fun <T : Any> handleListFooter(
    key: String,
    state: RefreshState,
    collectAsLazyPagingItems: LazyPagingItems<T>,
) {
    collectAsLazyPagingItems.apply {
        when (loadState.append) {
            is LoadState.Loading -> {
                Log.e("ssk", "加载更多，底部loading, state = ${state.type}, key=$key")
                state.type = RefreshType.LOAD_MORE_ING
            }
            is LoadState.Error -> {
                Log.e("ssk", "加载更多异常, state = ${state.type}, key=$key")
                state.finishLoadMore(false)
            }
            LoadState.NotLoading(endOfPaginationReached = true) -> {
                if (collectAsLazyPagingItems.itemCount > 0) {
                    Log.e("ssk", "加载更多---已经没有更多数据了, state = ${state.type}, key=$key")
                    state.noMoreData(true)
                }
            }
            LoadState.NotLoading(endOfPaginationReached = false) -> {
                Log.e("ssk", "加载更多---还有更多数据了, state = ${state.type}, key=$key")
                state.finishLoadMore(true)
            }
            else -> {}
        }
    }
}


class PagingStateHolderViewModel<T : Any> : ViewModel() {

    // 刷新状态，记录在PagingStateHolderViewModel中，避免结合horizontalPager切换tab时，由于页面重建，导致refreshState状态丢失
    val refreshState = RefreshState(type = RefreshType.IDLE)

    // 分页数据源
    private var pagingDataFlow: Flow<PagingData<T>>? = null

    // 首次进入该组件，数据还没加载成功，显示状态页面
    val showViewState = mutableStateOf(true)

    // 标记进入该组件，LoadState.Loading是否执行过了
    val hasLoadingDone = mutableStateOf(false)

    fun getPagingDataFlow(loadPagingDataFlowBlock: () -> Flow<PagingData<T>>): Flow<PagingData<T>> {
        if (pagingDataFlow == null) {
            pagingDataFlow = loadPagingDataFlowBlock.invoke()
        }
        return pagingDataFlow!!
    }

}
