package com.ssk.ncmusic.model

import androidx.annotation.Keep

/**
 * Created by ssk on 2022/5/14.
 */
@Keep
data class VideoGroupTabsResult(
    val data: List<VideoGroupTabBean>
) : BaseResult()

@Keep
data class VideoGroupTabBean(val id: Int, val name: String)