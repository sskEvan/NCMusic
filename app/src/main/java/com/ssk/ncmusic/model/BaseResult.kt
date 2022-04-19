package com.ssk.ncmusic.model

import java.io.Serializable

/**
 * Created by ssk on 2022/4/17.
 */
open class BaseResult(val code: Int = 0, val msg: String? = null) : Serializable