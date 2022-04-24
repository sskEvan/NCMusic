package com.ssk.ncmusic.hilt.module

import com.ssk.ncmusic.core.AppConfig
import com.ssk.ncmusic.hilt.RetrofitClient
import com.ssk.ncmusic.http.api.NCApi
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
    fun provideNCApi(@RetrofitClientModule.NCRetrofitClientQualifier retrofit: RetrofitClient): NCApi {
        return retrofit.setBaseUrl(AppConfig.BASE_URL).create(NCApi::class.java)
    }

}
