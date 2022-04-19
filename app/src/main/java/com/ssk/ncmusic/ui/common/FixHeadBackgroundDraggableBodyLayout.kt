package com.ssk.ncmusic.ui.common

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


/**
 * Created by ssk on 2022/4/19.
 */
private const val DragMultiplier = 0.6f

@Composable
fun rememberDragToggleState(
    dragStatus: DragStatus
): DragToggleState {
    return remember {
        DragToggleState(dragStatus = dragStatus)
    }.apply {
        this.dragStatus = dragStatus
    }
}

sealed class DragStatus {
    object Idle : DragStatus()
    object OverOpenTrigger : DragStatus()
    object Opened : DragStatus()
}

@Stable
class DragToggleState(
    dragStatus: DragStatus
) {
    private val _offset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    var dragStatus: DragStatus by mutableStateOf(dragStatus)
    var isDraggableInProgress: Boolean by mutableStateOf(false)
    val offset: Float get() = _offset.value

    internal suspend fun animateOffsetTo(offset: Float) {
        mutatorMutex.mutate {
            _offset.animateTo(offset, tween(300))
        }
    }

    internal suspend fun dispatchScrollDelta(delta: Float) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _offset.snapTo(_offset.value + delta)
        }
    }

    fun isIdle() = dragStatus == DragStatus.Idle
    fun isOverOpenTrigger() = dragStatus == DragStatus.OverOpenTrigger
    fun isOpened() = dragStatus == DragStatus.Opened

}

private class DragToggleNestedScrollConnection(
    private val state: DragToggleState,
    private val coroutineScope: CoroutineScope,
    private val onOverOpenTrigger: () -> Unit,
) : NestedScrollConnection {
    var enabled: Boolean = false
    var openTrigger: Float = 0f

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        // If swiping isn't enabled, return zero
        !enabled -> Offset.Zero
        // If we're refreshing, return zero
        //state.isOpen -> Offset.Zero
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
        //state.isOpen -> Offset.Zero
        // If the user is swiping down and there's y remaining, handle it
        source == NestedScrollSource.Drag && available.y > 0 -> onScroll(available)
        else -> Offset.Zero
    }

    private fun onScroll(available: Offset): Offset {
        state.isDraggableInProgress = true

        val newOffset = (available.y * DragMultiplier + state.offset).coerceAtLeast(0f)
        //val newOffset = available.y * DragMultiplier + state.offset

        val dragConsumed = newOffset - state.offset

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
        // If we're dragging, not currently refreshing and scrolled
        // past the trigger point, refresh!
        if (!state.isOverOpenTrigger() && state.offset >= openTrigger) {
            onOverOpenTrigger()
        }

        // Reset the drag in progress state
        state.isDraggableInProgress = false

        // Don't consume any velocity, to allow the scrolling layout to fling
        return Velocity.Zero
    }
}

@Composable
fun FixHeadBackgroundDraggableBodyLayout(
    state: DragToggleState,
    onOverOpenTrigger: () -> Unit,
    onOpened: () -> Unit,
    dragEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    triggerRadio: Float = 0.6f,
    maxDragRadio: Float = 1f,
    headBackgroundComponent: @Composable (state: DragToggleState, trigger: Float, maxDrag: Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val updatedOnOverOpenTrigger = rememberUpdatedState(onOverOpenTrigger)
    var backgroundHeight by remember {
        mutableStateOf(1)
    }
    val openTriggerPx = backgroundHeight * triggerRadio
    val maxDrag = backgroundHeight * maxDragRadio
    if(!state.isDraggableInProgress && maxDrag == state.offset && state.isOverOpenTrigger()) {
        onOpened()
    }
    LaunchedEffect(state.isDraggableInProgress, state.dragStatus) {
        Log.e("ssk", "LaunchedEffect  isDraggableInProgress=${state.isDraggableInProgress},dragStatus=${state.dragStatus} ")
        if (!state.isDraggableInProgress) {
            when (state.dragStatus) {
                DragStatus.Idle -> {
                    Log.e("ssk", "LaunchedEffect animateOffsetTo 0")
                    state.animateOffsetTo(0f)
                }
                DragStatus.OverOpenTrigger -> {
                    Log.e("ssk", "LaunchedEffect animateOffsetTo ${backgroundHeight.toFloat()}")
                    state.animateOffsetTo(maxDrag)
                }
                DragStatus.Opened -> {
                    Log.e("ssk", "LaunchedEffect animateOffsetTo 0")
                    state.animateOffsetTo(0f)
                }
            }
        }
    }

    val nestedScrollConnection = remember(state, coroutineScope) {
        DragToggleNestedScrollConnection(state, coroutineScope) {
            updatedOnOverOpenTrigger.value.invoke()
        }
    }.apply {
        this.enabled = dragEnabled
        this.openTrigger = openTriggerPx
    }

    Box(modifier.nestedScroll(connection = nestedScrollConnection)) {
        Box(
            Modifier
                // If we're not clipping to the padding, we use clipToBounds() before the padding()
                // modifier.
                //.let { if (!clipIndicatorToPadding) it.clipToBounds() else it }
                // Else, if we're are clipping to the padding, we use clipToBounds() after
                // the padding() modifier.
                //.let { if (clipIndicatorToPadding) it.clipToBounds() else it }
                .onGloballyPositioned {
                    backgroundHeight = it.size.height
                    Log.d("ssk", "indicatorHeight=${backgroundHeight}")
                }
        ) {
            Box(Modifier.align(Alignment.TopCenter)) {
                headBackgroundComponent(state, openTriggerPx, maxDrag)
            }
        }
        Box(
            modifier = Modifier.offset {
                IntOffset(0, state.offset.toInt().coerceAtMost(maxDrag.toInt()))
            },
        ) {
            content()
        }
    }
}