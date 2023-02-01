package com.ssk.ncmusic.ui.common.refresh

/**
 * Created by ssk on 2022/4/29.
 */
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.ssk.ncmusic.ui.common.refresh.indicator.header.CommonSwipeRefreshIndicator
import com.ssk.ncmusic.utils.cdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

private const val DragMultiplier = 0.5f

/**
 * Creates a [SwipeRefreshState] that is remembered across compositions.
 *
 * Changes to [isRefreshing] will result in the [SwipeRefreshState] being updated.
 *
 * @param isRefreshing the value for [SwipeRefreshState.isRefreshing]
 */
@Composable
fun rememberSwipeRefreshState(
    type: SwipeRefreshStateType
): SwipeRefreshState {
    return remember {
        SwipeRefreshState(
            type = type
        )
    }.apply {
        this.type = type
    }
}

sealed class SwipeRefreshStateType {
    object IDLE : SwipeRefreshStateType()
    object REFRESHING : SwipeRefreshStateType()
    object SUCCESS : SwipeRefreshStateType()
    object FAIL : SwipeRefreshStateType()
}

/**
 * A state object that can be hoisted to control and observe changes for [SwipeRefreshLayout].
 *
 * In most cases, this will be created via [rememberSwipeRefreshState].
 *
 * @param isRefreshing the initial value for [SwipeRefreshState.isRefreshing]
 */
@Stable
class SwipeRefreshState(
    type: SwipeRefreshStateType,
) {
    private val _indicatorOffset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    /**
     * Whether this [SwipeRefreshState] is currently refreshing or not.
     */
    var type: SwipeRefreshStateType by mutableStateOf(type)

    /**
     * Whether a swipe/drag is currently in progress.
     */
    var isSwipeInProgress: Boolean by mutableStateOf(false)
        internal set

    /**
     * The current offset for the indicator, in pixels.
     */
    val indicatorOffset: Float get() = _indicatorOffset.value

    internal suspend fun animateOffsetTo(targetOffset: Float, indicatorHeightPx: Float) {
        mutatorMutex.mutate {
            val duration = 400.coerceAtMost(((abs(targetOffset - indicatorOffset) / indicatorHeightPx) * 400).toInt())
            _indicatorOffset.animateTo(targetOffset, tween(duration))
        }
    }

    /**
     * Dispatch scroll delta in pixels from touch events.
     */
    internal suspend fun dispatchScrollDelta(delta: Float) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _indicatorOffset.snapTo(_indicatorOffset.value + delta)
        }
    }

    fun isRefreshing() = type == SwipeRefreshStateType.REFRESHING
    fun isIdle() = type == SwipeRefreshStateType.IDLE
    fun isSuccess() = type == SwipeRefreshStateType.SUCCESS
    fun isFail() = type == SwipeRefreshStateType.FAIL

    suspend fun resetOffset() {
        _indicatorOffset.snapTo(0f)
    }
}

private class SwipeRefreshNestedScrollConnection(
    private val state: SwipeRefreshState,
    private val coroutineScope: CoroutineScope,
    private val indicatorHeightPx: Float,
    private val maxDragRadio: Float,
    private val onIdle: () -> Unit,
    private val onRefresh: () -> Unit,
) : NestedScrollConnection {
    var enabled: Boolean = false
    var refreshTrigger: Float = 0f

    private fun isIndicatorAllShow() = state.indicatorOffset >= indicatorHeightPx
    private fun isIndicatorDragMax() = state.indicatorOffset == indicatorHeightPx * maxDragRadio
    private fun isIndicatorAllHide() = state.indicatorOffset == 0f

    /**
     * 父布局先处理
     */
    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (!enabled) {
            return Offset.Zero
        } else {
            if (!state.isIdle()) {  // 非空闲状态
                return Offset(0f, available.y)
            } else {
                if (source == NestedScrollSource.Drag) {
                    return if (source == NestedScrollSource.Drag && available.y < 0) {
                        if (!isIndicatorAllHide()) {
                            onScroll(available)
                        } else {
                            Offset.Zero
                        }
                    } else {
                        Offset.Zero
                    }
                } else {
                    return Offset.Zero
                }
            }
        }
    }

    /**
     * 父布局处理子布局处理后的事件
     */
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (!enabled) {
            return Offset.Zero
        } else {
            if (!state.isIdle()) {  // 非空闲状态
                return Offset(0f, available.y)
            } else {
                if (source == NestedScrollSource.Drag) {
                    return if (source == NestedScrollSource.Drag && available.y > 0) {
                        if (!isIndicatorDragMax()) {
                            onScroll(available)
                        } else {
                            Offset.Zero
                        }
                    } else {
                        Offset.Zero
                    }
                } else {
                    return Offset.Zero
                }
            }
        }
    }

    private fun onScroll(available: Offset): Offset {
        state.isSwipeInProgress = true

        val newOffset = (available.y * DragMultiplier + state.indicatorOffset).coerceAtLeast(0f)
        val dragConsumed = newOffset - state.indicatorOffset

        return if (dragConsumed.absoluteValue >= 0.5f) {
            coroutineScope.launch {
                state.dispatchScrollDelta(dragConsumed)
            }
            // Return the consumed Y
            Offset(x = 0f, y = dragConsumed / DragMultiplier)
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {

        if (enabled) {
            if (state.isIdle()) {
                if (isIndicatorAllShow()) {
                    if (state.indicatorOffset == indicatorHeightPx) {
                        onRefresh()
                    }
                    state.isSwipeInProgress = false
                    return if (available.y < 0) {
                        available
                    } else {
                        Velocity.Zero
                    }
                } else {
                    if (!isIndicatorAllHide()) {
                        if (!state.isSwipeInProgress || !state.isIdle()) {
                            onIdle()
                            state.isSwipeInProgress = false
                            return Velocity.Zero
                        } else {
                            coroutineScope.launch {
                                state.animateOffsetTo(0f, indicatorHeightPx)
                            }
                            return available
                        }
                    }
                    return Velocity.Zero
                }
            } else {
                state.isSwipeInProgress = false
                return available
            }
        } else {
            return Velocity.Zero
        }

        // Reset the drag in progress state
    }
}

/**
 * A layout which implements the swipe-to-refresh pattern, allowing the user to refresh content via
 * a vertical swipe gesture.
 *
 * This layout requires its content to be scrollable so that it receives vertical swipe events.
 * The scrollable content does not need to be a direct descendant though. Layouts such as
 * [androidx.compose.foundation.lazy.LazyColumn] are automatically scrollable, but others such as
 * [androidx.compose.foundation.layout.Column] require you to provide the
 * [androidx.compose.foundation.verticalScroll] modifier to that content.
 *
 * Apps should provide a [onRefresh] block to be notified each time a swipe to refresh gesture
 * is completed. That block is responsible for updating the [state] as appropriately,
 * typically by setting [SwipeRefreshState.isRefreshing] to `true` once a 'refresh' has been
 * started. Once a refresh has completed, the app should then set
 * [SwipeRefreshState.isRefreshing] to `false`.
 *
 * If an app wishes to show the progress animation outside of a swipe gesture, it can
 * set [SwipeRefreshState.isRefreshing] as required.
 *
 * This layout does not clip any of it's contents, including the indicator. If clipping
 * is required, apps can provide the [androidx.compose.ui.draw.clipToBounds] modifier.
 *
 * @sample com.google.accompanist.sample.swiperefresh.SwipeRefreshSample
 *
 * @param state the state object to be used to control or observe the [SwipeRefreshLayout] state.
 * @param onRefresh Lambda which is invoked when a swipe to refresh gesture is completed.
 * @param modifier the modifier to apply to this layout.
 * @param swipeEnabled Whether the the layout should react to swipe gestures or not.
 * @param indicator the indicator that represents the current state. By default this
 * will use a [OfficialSwipeRefreshIndicator].
 * @param clipIndicatorToPadding Whether to clip the indicator to [indicatorPadding]. If false is
 * provided the indicator will be clipped to the [content] bounds. Defaults to true.
 * @param content The content containing a scroll composable.
 */
@Composable
fun SwipeRefreshLayout(
    state: SwipeRefreshState,
    onRefresh: () -> Unit,
    onIdle: () -> Unit,
    modifier: Modifier = Modifier,
    swipeEnabled: Boolean = true,
    refreshTriggerRadio: Float = 1.0f,
    maxDragRadio: Float = 2f,
    indicatorHeight: Dp = 160.cdp,
    indicator: @Composable (state: SwipeRefreshState, refreshTrigger: Float, maxDrag: Float) -> Unit = { state, trigger, maxDrag ->
        CommonSwipeRefreshIndicator(state, trigger, maxDrag)
    },
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val updatedOnRefresh = rememberUpdatedState(onRefresh)
    val updateOnIdle = rememberUpdatedState(onIdle)
    val indicatorHeightPx = with(LocalDensity.current) { indicatorHeight.toPx() }
    val refreshTriggerPx = indicatorHeightPx * refreshTriggerRadio
    val maxDrag = indicatorHeightPx * maxDragRadio

    // Our LaunchedEffect, which animates the indicator to its resting position
    if (swipeEnabled) {
        HandleSwipeIndicatorOffset(state, indicatorHeightPx, onIdle = {
            updateOnIdle.value.invoke()
        }) {
            updatedOnRefresh.value.invoke()
        }
    }

    // Our nested scroll connection, which updates our state.
    val nestedScrollConnection = remember(state, coroutineScope) {
        SwipeRefreshNestedScrollConnection(state, coroutineScope, indicatorHeightPx, maxDragRadio, onIdle = {
            updateOnIdle.value.invoke()
        }) {
            // On refresh, re-dispatch to the update onRefresh block
            updatedOnRefresh.value.invoke()
        }
    }.apply {
        this.enabled = swipeEnabled
        this.refreshTrigger = refreshTriggerPx
    }

    Box(modifier.nestedScroll(connection = nestedScrollConnection)) {

        Box(Modifier.align(Alignment.TopCenter)
            .let { if (isHeaderNeedClip(state, indicatorHeightPx)) it.clipToBounds() else it }) {
            indicator(state, refreshTriggerPx, maxDrag)
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        0,
                        state.indicatorOffset
                            .toInt()
                            .coerceAtMost(maxDrag.toInt())
                    )
                },
        ) {
            content()
        }

    }
}

private fun isHeaderNeedClip(state: SwipeRefreshState, indicatorHeight: Float): Boolean {
    return state.indicatorOffset < indicatorHeight
}

@Composable
fun HandleSwipeIndicatorOffset(
    state: SwipeRefreshState, indicatorHeightPx: Float,
    onIdle: () -> Unit, onRefresh: () -> Unit
) {
    LaunchedEffect(state.isSwipeInProgress, state.type) {
        if (!state.isSwipeInProgress) {
            when (state.type) {
                SwipeRefreshStateType.REFRESHING -> {
                    if (state.indicatorOffset != indicatorHeightPx) {
                        state.animateOffsetTo(indicatorHeightPx, indicatorHeightPx)
                    }
                }
                SwipeRefreshStateType.IDLE -> {
                    if (state.indicatorOffset != 0f) {
                        if (state.indicatorOffset > indicatorHeightPx) {
                            state.animateOffsetTo(indicatorHeightPx, indicatorHeightPx)
                            onRefresh()
                        } else if (state.indicatorOffset < indicatorHeightPx) {
                            state.animateOffsetTo(0f, indicatorHeightPx)
                        }
                    }
                }
                SwipeRefreshStateType.SUCCESS, SwipeRefreshStateType.FAIL -> {
                    state.animateOffsetTo(indicatorHeightPx, indicatorHeightPx)
                    delay(300)
                    if (state.indicatorOffset != 0f) {
                        state.animateOffsetTo(0f, indicatorHeightPx)
                        onIdle()
                    }
                }
            }
        }
    }
}

