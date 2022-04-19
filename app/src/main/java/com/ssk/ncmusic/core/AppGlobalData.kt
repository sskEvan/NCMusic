package com.ssk.ncmusic.core

import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.utils.kvCacheParcelable

/**
 * Created by ssk on 2022/4/17.
 */
object AppGlobalData {

    var sLoginResult by kvCacheParcelable(LoginResult::class.java)

}