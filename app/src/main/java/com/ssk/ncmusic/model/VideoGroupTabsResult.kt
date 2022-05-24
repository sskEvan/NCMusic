package com.ssk.ncmusic.model

/**
 * Created by ssk on 2022/5/14.
 */
data class VideoGroupTabsResult(
    val data: List<VideoGroupTabBean>
) : BaseResult()

data class VideoGroupTabBean(val id: Int, val name: String)