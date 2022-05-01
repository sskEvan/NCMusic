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
import com.ssk.ncmusic.ui.common.refresh.classic.header.ClassicSwipeRefreshIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
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
        Log.d("ssk", "--------rememberSwipeRefreshState apply type = ${type}")
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

    internal suspend fun animateOffsetTo(offset: Float) {
        mutatorMutex.mutate {
            _indicatorOffset.animateTo(offset, tween(300))
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
    private val onRefresh: () -> Unit,
) : NestedScrollConnection {
    var enabled: Boolean = false
    var refreshTrigger: Float = 0f
    private var lastSwipeInProgressChangeTimeStamp = 0L
    private val MIN_SWIPE_CHANGE_TIME = 20

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        // If swiping isn't enabled, return zero
        !enabled -> Offset.Zero
        // If we're refreshing, return zero
        !state.isIdle() -> Offset.Zero
        // If the user is swiping up, handle it
        source == NestedScrollSource.Drag && available.y < 0 -> onScroll(available)
        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        // If swiping isn't enabled, return zero
        !enabled -> Offset.Zero
        // If we're refreshing, return zero
        !state.isIdle() -> Offset.Zero
        // If the user is swiping down and there's y remaining, handle it
        source == NestedScrollSource.Drag && available.y > 0 -> onScroll(available)
        else -> Offset.Zero
    }

    private fun onScroll(available: Offset): Offset {
        lastSwipeInProgressChangeTimeStamp = Date().time
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
        val curTimeStamp = Date().time
        val timeDiff = curTimeStamp - lastSwipeInProgressChangeTimeStamp

        if (lastSwipeInProgressChangeTimeStamp != 0L && timeDiff <= MIN_SWIPE_CHANGE_TIME) {
            lastSwipeInProgressChangeTimeStamp = curTimeStamp
            coroutineScope.launch(Dispatchers.IO) {
                val delay = MIN_SWIPE_CHANGE_TIME - timeDiff
                delay(delay)

                // If we're dragging, not currently refreshing and scrolled
                // past the trigger point, refresh!
                if (state.isIdle() && state.indicatorOffset >= refreshTrigger) {
                    onRefresh()
                }
                // Reset the drag in progress state
                state.isSwipeInProgress = false
            }
        } else {
            lastSwipeInProgressChangeTimeStamp = curTimeStamp
            // If we're dragging, not currently refreshing and scrolled
            // past the trigger point, refresh!
            if (state.isIdle() && state.indicatorOffset >= refreshTrigger) {
                onRefresh()
            }
            // Reset the drag in progress state
            state.isSwipeInProgress = false
        }

        // Don't consume any velocity, to allow the scrolling layout to fling
        return Velocity.Zero
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
    indicatorHeight: Dp = 60.dp,
    indicator: @Composable (state: SwipeRefreshState, refreshTrigger: Float, maxDrag: Float) -> Unit = { state, trigger, maxDrag ->
        ClassicSwipeRefreshIndicator(state, trigger, maxDrag)
    },
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val updatedOnRefresh = rememberUpdatedState(onRefresh)
    val indicatorHeightPx = with(LocalDensity.current) { indicatorHeight.toPx() }
    val refreshTriggerPx = indicatorHeightPx * refreshTriggerRadio
    val maxDrag = indicatorHeightPx * maxDragRadio

    // Our LaunchedEffect, which animates the indicator to its resting position
    if (swipeEnabled) {
        HandleSwipeIndicatorOffset(state, indicatorHeightPx, onIdle)
    }

    // Our nested scroll connection, which updates our state.
    val nestedScrollConnection = remember(state, coroutineScope) {
        SwipeRefreshNestedScrollConnection(state, coroutineScope) {
            // On refresh, re-dispatch to the update onRefresh block
            updatedOnRefresh.value.invoke()
        }
    }.apply {
        this.enabled = swipeEnabled
        this.refreshTrigger = refreshTriggerPx
    }

    Box(modifier.nestedScroll(connection = nestedScrollConnection)) {
        //Log.d("ssk", "state.indicatorOffset=${state.indicatorOffset}")

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
            //contentAlignment = Alignment.Center
        ) {
            content()
        }

    }
}

private fun isHeaderNeedClip(state: SwipeRefreshState, indicatorHeight: Float): Boolean {
    return state.indicatorOffset < indicatorHeight
}

@Composable
fun HandleSwipeIndicatorOffset(state: SwipeRefreshState, indicatorHeightPx: Float, onIdle: () -> Unit) {
    LaunchedEffect(state.isSwipeInProgress, state.type) {
        Log.e("ssk", "LaunchedEffect  isSwipeInProgress=${state.isSwipeInProgress},state.type=${state.type} ")
        if (!state.isSwipeInProgress) {
            when (state.type) {
                SwipeRefreshStateType.REFRESHING -> {
                    //startRefreshingTime =  SystemClock.uptimeMillis()
                    if (state.indicatorOffset != indicatorHeightPx) {
                        Log.e("ssk", "LaunchedEffect REFRESHING animateOffsetTo ${indicatorHeightPx}")
                        state.animateOffsetTo(indicatorHeightPx)
                    }
                }
                SwipeRefreshStateType.IDLE -> {
                    if (state.indicatorOffset != 0f) {
                        Log.e("ssk", "LaunchedEffect IDLE animateOffsetTo ${indicatorHeightPx}")
                        state.animateOffsetTo(0f)
                    }
                }
                SwipeRefreshStateType.SUCCESS, SwipeRefreshStateType.FAIL -> {
                    Log.e("ssk", "LaunchedEffect SUCCESS or FAIL animateOffsetTo ${indicatorHeightPx}")
                    state.animateOffsetTo(indicatorHeightPx)
                    delay(50)
                    if (state.indicatorOffset != 0f) {
                        state.animateOffsetTo(0f)
                        onIdle.invoke()
                    }
                }
            }
        }
    }
}

