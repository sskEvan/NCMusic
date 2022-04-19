package com.ssk.ncmusic.core.viewstate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.theme.AppColorsProvider

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun NoSuccessComponent(
    modifier: Modifier = Modifier.fillMaxSize(),
    loadDataBlock: (() -> Unit)? = null,
    specialRetryBlock: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    iconResId: Int = R.drawable.ic_empty,
    message: String = "加载失败"
) {
    Box(
        modifier = modifier
            .clickable {
                specialRetryBlock?.invoke() ?: loadDataBlock?.invoke()
            },
        contentAlignment = contentAlignment
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                painterResource(iconResId),
                null,
                tint = AppColorsProvider.current.primary,
                modifier = Modifier.size(100.dp)
            )
            if (!message.isEmpty()) {
                Text(
                    "$message,请点击重试",
                    fontSize = 14.sp,
                    color = AppColorsProvider.current.thirdText,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}