package com.ssk.ncmusic.core.viewstate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.gson.JsonParseException
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.ssk.ncmusic.R

/**
 * Created by ssk on 2022/4/17.
 */
/**
 * Created by ssk on 2022/1/11.
 * Description->页面状态切换组件, 根据viewStateLiveData，自动切换各种状态页面
 * @param modifier：页面布局修饰
 * @param viewStateLiveData：页面状态livedata
 * @param refreshFlag：刷新标志，refreshFlag=0是，页面和各个页面状态绑定，refreshFlag>0时，页面只显示正常数据，并且refreshFlag变化时会自动刷新页面数据
 * @param lifeCycleListener：生命周期监听
 * @param loadDataBlock：数据加载块
 * @param specialRetryBlock：特殊的重试请求代码块,没设置时重试逻辑将直接调用loadDataBlock
 * @param viewStateComponentModifier: 状态页面修饰
 * @param viewStateContentAlignment：状态页面居中方式
 * @param customEmptyComponent：自定义空布局,没设置则使用默认空布局
 * @param customFailComponent：自定义失败布局,没设置则使用默认失败布局
 * @param customErrorComponent：自定义错误布局,没设置则使用默认错误布局
 * @param contentView：正常页面内容
 */
@Composable
fun <T> ViewStateComponent(
    modifier: Modifier = Modifier,
    viewStateLiveData: ViewStateLiveData<T>?,
    refreshFlag: Int = 0,
    lifeCycleListener: ComposeLifeCycleListener? = null,
    loadDataBlock: (() -> Unit)? = null,
    specialRetryBlock: (() -> Unit)? = null,
    viewStateComponentModifier: Modifier = Modifier.fillMaxSize(),
    viewStateContentAlignment: Alignment = Alignment.Center,
    customEmptyComponent: @Composable (() -> Unit)? = null,
    customFailComponent: @Composable ((errorMessage: String?) -> Unit)? = null,
    customErrorComponent: @Composable ((errorMessage: Pair<String, Int>) -> Unit)? = null,
    contentView: @Composable BoxScope.(data: T) -> Unit
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

    var successData = remember {
        mutableStateOf<T?>(null)
    }


    if (viewStateLiveData != null) {
        val viewState by viewStateLiveData.observeAsState()
        if (refreshFlag == 0) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (viewState) {
                    is ViewState.Loading -> {
                        LoadingComponent(
                            modifier = viewStateComponentModifier,
                            contentAlignment = viewStateContentAlignment
                        )
                    }
                    is ViewState.Success -> {
                        successData.value = (viewState as ViewState.Success<T>).data!!
                        contentView(successData.value!!)
                    }
                    is ViewState.Empty -> {
                        customEmptyComponent?.invoke() ?: NoSuccessComponent(
                            loadDataBlock = loadDataBlock,
                            contentAlignment = viewStateContentAlignment,
                            specialRetryBlock = specialRetryBlock,
                            modifier = viewStateComponentModifier
                        )
                    }
                    is ViewState.Fail -> {
                        customFailComponent?.invoke((viewState as ViewState.Fail).errorMsg) ?: NoSuccessComponent(
                            modifier = viewStateComponentModifier,
                            message = "${(viewState as ViewState.Fail).errorMsg} 点我重试",
                            loadDataBlock = loadDataBlock,
                            specialRetryBlock = specialRetryBlock,
                            contentAlignment = viewStateContentAlignment
                        )
                    }
                    is ViewState.Error -> {
                        if (customErrorComponent != null) {
                            customErrorComponent.invoke(getErrorMessagePair((viewState as ViewState.Error).exception))
                        } else {
                            val errorMessagePair = getErrorMessagePair((viewState as ViewState.Error).exception)
                            NoSuccessComponent(
                                modifier = viewStateComponentModifier,
                                message = errorMessagePair.first,
                                iconResId = errorMessagePair.second,
                                loadDataBlock = loadDataBlock,
                                specialRetryBlock = specialRetryBlock,
                                contentAlignment = viewStateContentAlignment,
                            )
                        }
                    }
                    else -> {
                        loadDataBlock?.invoke()
                    }
                }
            }
        } else {
            // refreshFlag变化时自动刷新数据
            LaunchedEffect(refreshFlag) {
                loadDataBlock?.invoke()
            }

            if (viewState is ViewState.Success) {
                successData.value = (viewState as ViewState.Success<T>).data!!
            }

            successData.value?.let {
                Box(
                    modifier = modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    contentView(it)
                }
            }
        }
    }
}


fun getErrorMessagePair(exception: Throwable): Pair<String, Int> {
    return when (exception) {
        is ConnectException,
        is UnknownHostException -> {
            Pair("网络连接失败", R.drawable.ic_network_error)
        }
        is SocketTimeoutException -> {
            Pair("网络连接超时", R.drawable.ic_network_error)
        }
        is JsonParseException -> {
            Pair("数据解析错误", R.drawable.ic_network_error)
        }
        else -> {
            Pair("未知错误", R.drawable.ic_network_error)
        }
    }
}
