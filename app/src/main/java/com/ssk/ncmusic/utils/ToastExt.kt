package com.ssk.ncmusic.utils

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import android.widget.Toast
import java.lang.Exception

/**
 * Created by ssk on 2022/4/17.
 */
private var toast: Toast? = null
private var application: Application? = null

/**
 * 弹出Toast信息。如果不是在主线程中调用此方法，Toast信息将会不显示。
 *
 * @param content
 * Toast中显示的内容
 */
@SuppressLint("ShowToast")
@JvmOverloads
fun showToast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (toast == null) {
            toast = Toast.makeText(getApplication(), content, duration)
        } else {
            toast?.setText(content)
        }
        toast?.show()
    }
}

private fun getApplication(): Application {
    if (application == null) {
        try {
            application = Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as Application
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    return application!!
}
