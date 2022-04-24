package com.ssk.ncmusic.http.intercept

import com.ssk.ncmusic.core.AppGlobalData
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by ssk on 2022/4/24.
 */
class CookieIntercept : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val mLoginResult = AppGlobalData.sLoginResult
        if (mLoginResult != null) {
            val request = chain.request()
            val url = request.url.toString() + "&cookie=" + mLoginResult.cookie
            val builder = request.newBuilder()
            builder.get().url(url)
            val newRequest = builder.build()
            return chain.proceed(newRequest);
        }
        return chain.proceed(chain.request())
    }
}