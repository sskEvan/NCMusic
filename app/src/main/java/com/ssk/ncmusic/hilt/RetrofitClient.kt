package com.ssk.ncmusic.hilt

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created by ssk on 2022/4/17.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitClient {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NCRetrofitClient

    @Provides
    @Singleton
    @NCRetrofitClient
    fun provideNCRetrofitClient(): LibCoroutineNetwork {
        //新建log拦截器
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e("OkHttp", "OkHttp====:$message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder().apply {
            writeTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
        }
        return LibCoroutineNetwork(
            Retrofit.Builder().client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
        )
    }
}