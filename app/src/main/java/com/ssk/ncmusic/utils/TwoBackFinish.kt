package com.ssk.ncmusic.utils

/**
 * Created by ssk on 2022/4/17.
 */
class TwoBackFinish {
    companion object {
        var mExitTime: Long = 0
    }

    fun execute(finish:() -> Unit) {
        when {
            /**
             * 点击两次退出程序 有事件间隔，间隔内退出程序，否则提示
             */
            (System.currentTimeMillis() - mExitTime) > 1500 -> {
                showToast("再按一次退出程序")
                mExitTime = System.currentTimeMillis()
            }
            else -> {
                finish()
            }
        }
    }
}