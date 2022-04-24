package com.ssk.ncmusic.hilt.entrypoint

import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.core.NCApplication
import dagger.hilt.EntryPoints

/**
 * Created by ssk on 2022/4/24.
 */

object EntryPointFinder {
    fun getNCApi(): NCApi {
        val entryPoint = EntryPoints.get(NCApplication.context, NCApiEntryPoint::class.java)
        return entryPoint.getNCApi()
    }
}

