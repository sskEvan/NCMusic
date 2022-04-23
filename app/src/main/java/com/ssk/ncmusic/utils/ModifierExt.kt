package com.ssk.ncmusic.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Created by ssk on 2022/3/2.
 */

/**
 * 带水波纹点击事件
 * [enableRipple]:是否支持水波纹效果
 * [rippleColor]:水波纹颜色
 * [onClick]:点击回调
 */
@Composable
fun Modifier.onClick(enableRipple: Boolean = true, rippleColor: Color = Color.Unspecified, onClick: () -> Unit) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = if (enableRipple) rememberRipple(color = rippleColor, bounded = true) else null
) {
    onClick()
}