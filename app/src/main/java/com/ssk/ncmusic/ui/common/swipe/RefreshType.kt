package com.ssk.ncmusic.ui.common.swipe

/**
 * Created by ssk on 2023/2/1.
 */
sealed class RefreshType {
    object IDLE : RefreshType()
    object PULL_TO_REFRESH : RefreshType()
    object RELEASE_TO_REFRESH : RefreshType()
    object REFRESHING : RefreshType()
    object REFRESH_SUCCESS : RefreshType()
    object REFRESH_FAIL : RefreshType()
    object LOAD_MORE_ING : RefreshType()
    object LOAD_MORE_SUCCESS : RefreshType()
    object LOAD_MORE_FAIL : RefreshType()
}
