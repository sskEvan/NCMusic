package com.ssk.ncmusic.utils

/**
 * Created by ssk on 2022/4/23.
 */
object StringUtil {

    fun friendlyNumber(num: Number): String {
        if(num.toLong() < 10000) {
            return num.toString()
        }else if(num.toLong() < 100000000) {
            val result = num.toLong() / 10000
            return result.toString() + "万"
        }else if(num.toLong() >= 100000000) {
            val result = num.toLong() / 100000000
            return result.toString() + "亿"
        }
        return num.toString()
    }
}