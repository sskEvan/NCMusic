package com.ssk.ncmusic.model

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Created by ssk on 2022/4/17.
 */
@Keep
open class BaseResult(val code: Int? = 0, val message: String? = null) : Serializable {
    open fun resultOk(): Boolean {
        return code == 200
    }
}