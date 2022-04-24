package com.ssk.ncmusic.hilt

import retrofit2.Retrofit

/**
 * Created by ssk on 2022/4/17.
 */
class RetrofitClient constructor(private val builder: Retrofit.Builder) {

    private val retrofitCache = hashMapOf<String, Retrofit>()

    fun setBaseUrl(baseUrl: String): Retrofit {
        val cacheRetrofit = retrofitCache[baseUrl]
        return if (cacheRetrofit != null) {
            cacheRetrofit
        } else {
            val retrofit = builder
                .baseUrl(baseUrl)
                .build()
            retrofitCache[baseUrl] = retrofit
            retrofit
        }
    }

}