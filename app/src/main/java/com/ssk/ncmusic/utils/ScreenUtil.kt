package com.ssk.ncmusic.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.ssk.ncmusic.core.NCApplication

/**
 * Created by ssk on 2022/4/29.
 */
object ScreenUtil {
    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        val wm = NCApplication.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        val wm = NCApplication.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }

    /**
     * 获取底部navigationBar高度
     *
     * @return 底部导航航高度
     */
    fun getNavigationBarHeight(): Int {
        return try {
            val resources = NCApplication.context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        } catch (e: Exception) {
            0
        }
    }
}