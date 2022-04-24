package com.ssk.ncmusic.hilt.entrypoint

import com.ssk.ncmusic.http.api.NCApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by ssk on 2022/4/24.
 */

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NCApiEntryPoint {
    //@ApiServiceModule.NCApiInterfaceQualifier
    fun getNCApi(): NCApi
}