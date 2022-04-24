package com.ssk.ncmusic.hilt.module

import android.util.Log
import com.ssk.ncmusic.hilt.RetrofitClient
import com.ssk.ncmusic.http.intercept.CookieIntercept
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
object RetrofitClientModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NCRetrofitClientQualifier

    @Provides
    @Singleton
    @NCRetrofitClientQualifier
    fun provideNCRetrofitClient(): RetrofitClient {
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
            addInterceptor(CookieIntercept())
        }
        return RetrofitClient(
            Retrofit.Builder().client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
        )
    }
}