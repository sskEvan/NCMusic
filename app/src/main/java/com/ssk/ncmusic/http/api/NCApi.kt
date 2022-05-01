package com.ssk.ncmusic.http.api

import com.ssk.ncmusic.model.*
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by ssk on 2022/4/17.
 */
interface NCApi {

    @GET("login")
    suspend fun loginByEmail(
        @Query("email") email: String,
        @Query("password") password: String = "",
        @Query("md5_password") passwordMD5: String
    ): LoginResult

    @GET("login/cellphone")
    suspend fun loginByPassword(
        @Query("phone") phone: String,
        @Query("password") password: String = "",
        @Query("md5_password") passwordMD5: String
    ): LoginResult

    @GET("user/playlist")
    suspend fun getUserPlayList(@Query("uid") uid: String): UserPlaylistResult

    @GET("playlist/detail")
    suspend fun getPlaylistDetail(@Query("id") id: Long): PlaylistDetailResult

    @GET("song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetailResult

    @GET("/song/url")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("br") br: Int = 128000
    ): SongUrlBean

    @GET("/comment/music")
    suspend fun getSongComment(
        @Query("id") id: Long,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int
    ): SongCommentResult

    @GET("/comment/new")
    suspend fun getNewComment(
        @Query("id") id: Long,
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sortType") sortType : Int,
        @Query("cursor") cursor : String,
        ): NewCommentResult
}