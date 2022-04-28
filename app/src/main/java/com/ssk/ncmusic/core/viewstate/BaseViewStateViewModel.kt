package com.ssk.ncmusic.core.viewstate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssk.ncmusic.model.BaseResult
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/17.
 */

typealias ViewStateMutableLiveData<T> = MutableLiveData<ViewState<T>>
typealias ViewStateLiveData<T> = LiveData<ViewState<T>>

open class BaseViewStateViewModel : ViewModel() {

    /**
     * livedata通用处理
     */
    protected fun <T : BaseResult> launch(
        liveData: ViewStateMutableLiveData<T>? = null,
        handleResult: ((T) -> Unit)? = null,
        judgeEmpty: ((T) -> Boolean)? = null,
        call: suspend () -> T
    ) {
        viewModelScope.launch {
            runCatching {
                liveData?.let {
                    it.value = ViewState.Loading
                }
                call()
            }.onSuccess { result ->
                if (result.code == 200) {
                    if (judgeEmpty?.invoke(result) == true) {
                        liveData?.let {
                            it.value = ViewState.Empty
                        }
                    } else {
                        handleResult?.invoke(result)
                        liveData?.let {
                            it.value = ViewState.Success(result)
                        }
                    }
                } else {
                    liveData?.let {
                        it.value = ViewState.Fail(result.code.toString(), result.msg ?: "请求出错")
                    }
                }
            }.onFailure { e ->
                liveData?.let {
                    it.value = ViewState.Error(e)
                }
            }
        }
    }
}