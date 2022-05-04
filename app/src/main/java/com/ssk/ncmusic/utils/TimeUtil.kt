package com.ssk.ncmusic.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

/**
 * Created by ssk on 2022/5/2.
 */
@SuppressLint("SimpleDateFormat")
enum class FormatterEnum(val value: SimpleDateFormat) {
    YYYY_MM_DD(SimpleDateFormat("yyyy-MM-dd")),
    YYYYMMDD(SimpleDateFormat("yyyyMMdd")),
    YYYY_MM_DD__HH_MM(SimpleDateFormat("yyyy-MM-dd HH:mm")),
    YYYY_MM_DD__HH_MM_SS(SimpleDateFormat("yyyy-MM-dd HH:mm:ss")),
    YYYYMMDD__HH_MM(SimpleDateFormat("yyyyMMdd HH:mm")),
    YYYYMMDD__HH_MM_SS(SimpleDateFormat("yyyyMMdd HH:mm:ss")),
    HH_MM_SS(SimpleDateFormat("HH:mm:ss"))
}

object TimeUtil {
    fun parse(value: Long, formatter: FormatterEnum = FormatterEnum.YYYY_MM_DD): String {
        return formatter.value.format(value)
    }
}