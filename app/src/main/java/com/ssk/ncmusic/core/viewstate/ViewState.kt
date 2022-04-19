package com.ssk.ncmusic.core.viewstate

/**
 * Created by ssk on 2022/4/17.
 */
sealed class ViewState<out T> {
    object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    object Empty : ViewState<Nothing>()
    data class Fail(val errorCode: String, val errorMsg: String) : ViewState<Nothing>()
    data class Error(val exception: Throwable) : ViewState<Nothing>()
}