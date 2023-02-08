package com.ssk.ncmusic.core.viewstate.paging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ssk.ncmusic.model.BaseResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Created by ssk on 2022/1/11.
 * Description -> 分页扩展函数
 */

fun <R : BaseResult, I : Any> ViewModel.buildPager(
    config: AppPagingConfig = AppPagingConfig(),
    //judgeEmpty: ((R) -> Boolean)? = null,
    listSpan: Int = 1,
    transformListBlock: (r: R?) -> List<I>?,
    callBlock: suspend (page: Int, config: Int) -> R
): Flow<PagingData<I>> {

    return pager(config, 1) {
        val currentPage = it.key ?: 1
        val result = callBlock.invoke(currentPage, if (currentPage == 1) config.initialLoadSize else config.pageSize)
        if (result.resultOk()) {
            val responseList = transformListBlock.invoke(result) ?: emptyList()
            Log.e("ssk2", "responseList.size=${responseList.size}")

            val everyPageSize = config.pageSize
            val initPageSize = config.initialLoadSize
            val preKey = if (currentPage == 1) null else currentPage.minus(1)
            var nextKey: Int? = if (currentPage == 1) {
                (initPageSize / everyPageSize).plus(1)
            } else {
                currentPage.plus(1)
            }

            if (responseList.size * listSpan < everyPageSize || !config.enableLoadMore) {
                nextKey = null
            }

            PagingSource.LoadResult.Page(
                data = responseList,
                prevKey = preKey,
                nextKey = nextKey
            )
        } else {
            PagingSource.LoadResult.Error(PagingException(result.code?.toString()?: "-1", result.message?: "未知错误"))
        }
    }
}

fun <K : Any, V : Any> ViewModel.pager(
    config: AppPagingConfig = AppPagingConfig(),
    initialKey: K? = null,
    errorBlock: (() -> Unit)? = null,
    loadData: suspend (PagingSource.LoadParams<K>) -> PagingSource.LoadResult<K, V>
): Flow<PagingData<V>> {
    val baseConfig = PagingConfig(
        config.pageSize,
        initialLoadSize = config.initialLoadSize,
        prefetchDistance = config.prefetchDistance,
        maxSize = config.maxSize,
        enablePlaceholders = config.enablePlaceholders
    )
    return Pager(
        config = baseConfig,
        initialKey = initialKey
    ) {
        object : PagingSource<K, V>() {
            override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                val startRequestTime = Date().time
                return try {
                    val result = loadData.invoke(params)
                    val requestTimeCost = Date().time - startRequestTime
                    val delayTime = 0L.coerceAtLeast(config.minRequestCycle - requestTimeCost)
                    Log.e("ssk2", "delayTime = $delayTime, requestTimeCost=$requestTimeCost")
                    delay(delayTime)
                    result
                } catch (e: Exception) {
                    e.printStackTrace()
                    val requestTimeCost = Date().time - startRequestTime
                    val delayTime = 0L.coerceAtLeast(config.minRequestCycle - requestTimeCost)
                    Log.e("ssk2", "delayTime = $delayTime, requestTimeCost=$requestTimeCost")
                    delay(delayTime)
                    errorBlock?.invoke()
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<K, V>): K? {
                return initialKey
            }

        }
    }.flow.cachedIn(viewModelScope)
}

class PagingException(val errorCode: String, val errorMessage: String) : Exception("PagingException")
