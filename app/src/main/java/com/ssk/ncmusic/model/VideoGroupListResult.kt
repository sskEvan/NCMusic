package com.ssk.ncmusic.model

/**
 * Created by ssk on 2022/5/14.
 */
data class VideoGroupListResult(
    val data: List<VideoGroupListItemBean>
) : BaseResult()

data class VideoGroupListItemBean(val id: Int, val name: String)