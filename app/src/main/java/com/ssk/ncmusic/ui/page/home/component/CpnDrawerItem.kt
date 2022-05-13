package com.ssk.ncmusic.ui.page.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.onClick

/**
 * Created by ssk on 2022/5/13.
 */
@Composable
fun CpnDrawerItem(
    leftIconResId: Int,
    rightIconResId: Int,
    rightIconModifier: Modifier,
    title: AnnotatedString,
    onClick: (() -> Unit)? = null) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.cdp)
            .onClick {
                onClick?.invoke()
            }
            .padding(horizontal = 32.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonIcon(
            resId = leftIconResId,
            modifier = Modifier
                .size(40.cdp)
        )

        Text(
            text = title,
            modifier = Modifier.padding(start = 24.cdp),
            color = AppColorsProvider.current.firstText
        )

        Spacer(modifier = Modifier.weight(1f))

        CommonIcon(
            resId = rightIconResId,
            modifier = rightIconModifier
                .size(32.cdp),
            tint = AppColorsProvider.current.secondIcon
        )
    }
}


