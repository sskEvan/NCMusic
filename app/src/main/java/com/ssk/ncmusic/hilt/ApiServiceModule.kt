package com.ssk.ncmusic.hilt

import com.ssk.ncmusic.api.NCApi
import com.ssk.ncmusic.core.AppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by ssk on 2022/4/17.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    @Singleton
    fun provideMessageCenterApi(@RetrofitClient.NCRetrofitClient retrofit: LibCoroutineNetwork): NCApi {
        return retrofit.setBaseUrl(AppConfig.BASE_URL).create(NCApi::class.java)
    }
}