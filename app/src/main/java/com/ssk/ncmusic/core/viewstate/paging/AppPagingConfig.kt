package com.ssk.ncmusic.core.viewstate.paging

/**
 * Created by ssk on 2022/1/11.
 * Description -> app默认分页配置
 */

const val DEFAULT_EVERY_PAGE_SIZE = 20
const val DEFAULT_INITIAL_LOAD_SIZE = 20
const val DEFAULT_PREFETCH_DISTANCE = 4

data class AppPagingConfig(
    val pageSize: Int = DEFAULT_EVERY_PAGE_SIZE,
    val initialLoadSize: Int = DEFAULT_INITIAL_LOAD_SIZE,
    val prefetchDistance:Int = DEFAULT_PREFETCH_DISTANCE,
    val maxSize:Int = Int.MAX_VALUE,
    val enablePlaceholders:Boolean = false,
    val enableLoadMore: Boolean = true,
    val minRequestCycle: Long = 500L
)