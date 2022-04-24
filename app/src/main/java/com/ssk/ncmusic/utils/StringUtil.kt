package com.ssk.ncmusic.utils

import java.util.*

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

    fun formatMilliseconds(milliseconds: Int): String {
        val standardTime: String
        val seconds = milliseconds / 1000
        if (seconds <= 0) {
            standardTime = "00:00"
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60)
        } else if (seconds < 3600) {
            standardTime = java.lang.String.format(
                Locale.getDefault(),
                "%02d:%02d",
                seconds / 60,
                seconds % 60
            )
        } else {
            standardTime = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                seconds / 3600,
                seconds % 3600 / 60,
                seconds % 60
            )
        }
        return standardTime
    }
}