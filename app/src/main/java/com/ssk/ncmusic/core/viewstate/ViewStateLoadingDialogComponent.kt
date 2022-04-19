package com.ssk.ncmusic.core.viewstate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.utils.showToast

/**
 * Created by ssk on 2022/1/11.
 * Description->根据viewStateLiveData，自动弹出/隐藏加载框
 * @param viewStateLiveData：页面状态livedata
 * @param modifier：页面布局修饰
 * @param lifeCycleListener：生命周期监听
 * @param contentView：正常页面内容
 */
@Composable
fun <T> ViewStateLoadingDialogComponent(
    modifier: Modifier = Modifier,
    viewStateLiveData: ViewStateMutableLiveData<T>,
    lifeCycleListener: ComposeLifeCycleListener? = null,
    successBlock: ((data: T) -> Unit)? = null,
    contentView: @Composable BoxScope.() -> Unit
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
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

            onDispose {
                listener.onExitCompose(lifecycleOwner)
                lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
            }
        }
    }

    var showLoadingDialog by remember {
        mutableStateOf(false)
    }
    val viewState by viewStateLiveData.observeAsState()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        contentView()

        when (viewState) {
            is ViewState.Loading -> {
                LoadingDialog(showLoadingDialog)
            }
            is ViewState.Success -> {
                showLoadingDialog = false
                val data = (viewState as ViewState.Success<T>).data!!
                successBlock?.invoke(data)
            }
            is ViewState.Empty -> {
                showLoadingDialog = false
            }
            is ViewState.Fail -> {
                showLoadingDialog = false
                showToast((viewState as ViewState.Fail).errorMsg)
            }
            is ViewState.Error -> {
                showLoadingDialog = false
                showToast(getErrorMessagePair((viewState as ViewState.Error).exception).first)
            }
        }
    }
}