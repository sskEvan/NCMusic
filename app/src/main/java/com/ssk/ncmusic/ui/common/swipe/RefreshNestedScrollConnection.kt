package com.ssk.ncmusic.ui.common.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Created by ssk on 2023/2/1.
 */
internal class RefreshNestedScrollConnection(
    private val state: RefreshState,
    private val enableRefresh: Boolean,
    private val enableLoadMore: Boolean,
    private val scope: CoroutineScope,
    private val headerHeight: Float,
    private val footerHeight: Float,
    private val onRefresh: (() -> Unit)? = null,
) : NestedScrollConnection {

    companion object {
        internal const val ACTION_REFRESH = 1
        internal const val ACTION_LOAD_MORE = 2
    }

    private var isAnim = false
    private val damping = 0.5f
    private val headerMaxDragHeight = headerHeight.toInt() * 2

    private val headerIsShow by derivedStateOf { offset > 0 }
    private val headerIsAllShow by derivedStateOf { offset >= headerHeight }
    private val isHeaderDragMax by derivedStateOf { offset >= headerMaxDragHeight }

    private val footerIsShow by derivedStateOf { offset < 0 }
    private val footerIsAllShow by derivedStateOf { offset <= -footerHeight }
    private var isDragging = false

    private val offsetAnim = Animatable(0f)
    val offset get() = offsetAnim.value

    /**
     * 父布局先处理
     */
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!enableRefresh && !enableLoadMore) {
            return super.onPreScroll(available, source)
        }
        if (isDoingRefreshAction()) {
            return available
        }
        isDragging = true
        if (source == NestedScrollSource.Drag) {
            if (enableRefresh && allowTriggerRefresh()) {
                if (available.y < 0) {  // 向上滑动
                    if (headerIsShow) {
                        snapToOffset(ACTION_REFRESH, 0f.coerceAtLeast(offset + available.y * damping))
                        return available
                    }
                }
            }

            if (enableLoadMore) {
                if (allowTriggerLoadMore() || state.noMoreData) {
                    if (available.y > 0) {  // 向下滑动
                        if (footerIsShow) {
                            snapToOffset(ACTION_LOAD_MORE, 0f.coerceAtMost(offset + available.y * damping))
                            return available
                        }
                    }
                }
            }
        }
        return super.onPreScroll(available, source)
    }

    /**
     * 父布局处理子布局处理后的事件
     */
    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (!enableRefresh && !enableLoadMore) {
            return super.onPreScroll(available, source)
        }
        if (isDoingRefreshAction()) {
            return available
        }
        if (source == NestedScrollSource.Drag) {
            if (enableRefresh && allowTriggerRefresh() && state.type != RefreshType.LOAD_MORE_FAIL) {
                if (available.y > 0) {  // 向下滑动
                    if (!isHeaderDragMax) {
                        snapToOffset(ACTION_REFRESH, headerMaxDragHeight.toFloat().coerceAtMost(offset + available.y * damping))
                        return available
                    }
                }
            }

            if (enableLoadMore) {
                if (available.y < 0) {  // 向上滑动
                    if (!footerIsAllShow) {
                        snapToOffset(ACTION_LOAD_MORE, (-footerHeight).coerceAtLeast(offset + available.y * damping))
                        return available
                    }
                }
            }

        }
        return super.onPostScroll(consumed, available, source)

    }

    /**
     * 父布局先处理
     */
    override suspend fun onPreFling(available: Velocity): Velocity {
        if (!enableRefresh && !enableLoadMore) {
            return super.onPreFling(available)
        }
        if (isDoingRefreshAction()) {
            return available
        }

        if (enableRefresh && allowTriggerRefresh()) {
            if (available.y <= 0) {  // 向上滑动
                if (headerIsShow) {
                    if (headerIsAllShow) {
                        animateToOffset(ACTION_REFRESH, headerHeight, headerHeight)
                    } else {
                        animateToOffset(ACTION_REFRESH, 0f, headerHeight)
                    }
                    return available
                }
            }
        }

        if (enableLoadMore && allowTriggerLoadMore()) {
            if (available.y >= 0) {  // 向下滑动
                if (footerIsShow) {
                    if (state.noMoreData) {
                        animateToOffset(ACTION_LOAD_MORE, 0f, footerHeight)
                    } else {
                        animateToOffset(ACTION_LOAD_MORE, -footerHeight, footerHeight)
                    }
                    return available
                }
            }
        }

        return super.onPreFling(available)
    }

    /**
     * 父布局处理子布局处理后的事件
     */
    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (!enableRefresh && !enableLoadMore) {
            return super.onPostFling(consumed, available)
        }
        if (isDoingRefreshAction()) {
            return available
        }
        isDragging = false
        if (enableRefresh && allowTriggerRefresh() && state.type != RefreshType.LOAD_MORE_FAIL) {
            if (!isAnim && available.y >= 0) {  // 向下滑动
                if (headerIsAllShow) {
                    animateToOffset(ACTION_REFRESH, headerHeight, headerHeight)
                } else {
                    animateToOffset(ACTION_REFRESH, 0f, headerHeight)
                }
                return available
            }
        }
        if (enableLoadMore) {
            if (!isAnim && available.y < 0) {
                if (!footerIsAllShow) {
                    animateToOffset(ACTION_LOAD_MORE, -footerHeight, footerHeight)
                }
            }
        }
        return super.onPostFling(consumed, available)
    }

    internal fun animateToOffset(action: Int, targetOffset: Float, indicatorHeight: Float, delayTime: Long = 0L) {
        isAnim = true
        scope.launch {
            if (delayTime != 0L) {
                delay(delayTime)
            }
            val duration = 100.coerceAtLeast(300.coerceAtMost(((abs(targetOffset - offset) / indicatorHeight) * 300).toInt()))
            offsetAnim.animateTo(targetOffset, tween(duration)) {
                updateRefreshState(action, value)
            }
            isAnim = false
        }

    }

    private fun snapToOffset(action: Int, value: Float) {
        isAnim = false
        scope.launch {
            offsetAnim.snapTo(value)
            updateRefreshState(action, value)
        }
    }

    private fun updateRefreshState(action: Int, offset: Float) {
        if (action == ACTION_REFRESH) {
            if (!isDragging && offset == headerHeight && allowTriggerRefresh()) {
                onRefresh?.invoke()
            } else {
                if (offset <= 0) {
                    state.type = RefreshType.IDLE
                } else if (allowTriggerRefresh() && offset > 0 && offset < headerHeight) {
                    state.type = RefreshType.PULL_TO_REFRESH
                } else if (allowTriggerRefresh() && offset > headerHeight) {
                    state.type = RefreshType.RELEASE_TO_REFRESH
                }
            }
        } else if (action == ACTION_LOAD_MORE) {
            if (offset == 0f && state.type != RefreshType.LOAD_MORE_FAIL) {
                state.type = RefreshType.IDLE
            }
        }
    }

    private fun isDoingRefreshAction() =
        state.type == RefreshType.REFRESHING || state.type == RefreshType.REFRESH_SUCCESS || state.type == RefreshType.REFRESH_FAIL

    private fun allowTriggerRefresh() = (state.type != RefreshType.REFRESHING
            && state.type != RefreshType.REFRESH_SUCCESS
            && state.type != RefreshType.REFRESH_FAIL)

    private fun allowTriggerLoadMore() = (state.type != RefreshType.LOAD_MORE_ING
            && state.type != RefreshType.LOAD_MORE_SUCCESS
            && state.type != RefreshType.LOAD_MORE_FAIL)
}
