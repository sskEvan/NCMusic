package com.ssk.ncmusic.ui.common.swipe

import androidx.compose.runtime.*

/**
 * Created by ssk on 2023/2/1.
 */
@Stable
class RefreshState(
    private val usePaging3: Boolean? = false,
    type: RefreshType,
) {
    var type: RefreshType by mutableStateOf(type)
    var noMoreData by mutableStateOf(false)

    fun finishRefresh(success: Boolean = true) {
        if (type == RefreshType.REFRESHING) {
            type = if (success) {
                noMoreData(false)
                RefreshType.REFRESH_SUCCESS
            } else {
                RefreshType.REFRESH_FAIL
            }
        }
    }

    fun finishLoadMore(success: Boolean = true) {
        if (type == RefreshType.LOAD_MORE_ING) {
            type = if (success) {
                RefreshType.LOAD_MORE_SUCCESS
            } else {
                RefreshType.LOAD_MORE_FAIL
            }
        }
    }

    fun noMoreData(noMoreData: Boolean) {
        if (this.noMoreData != noMoreData) {
            this.noMoreData = noMoreData
            type = RefreshType.LOAD_MORE_SUCCESS
        }
    }
}

@Composable
fun rememberRefreshState(
    usePaging3: Boolean? = false,
    type: RefreshType = RefreshType.IDLE
): RefreshState {
    return remember {
        RefreshState(
            usePaging3 = usePaging3,
            type = type
        )
    }.apply {
        this.type = type
    }
}