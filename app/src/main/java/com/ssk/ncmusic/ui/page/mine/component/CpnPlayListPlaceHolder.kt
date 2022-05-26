package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/5/26.
 */

@Composable
fun CpnPlayListPlaceHolder(tip: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.cdp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 24.cdp, bottomEnd = 24.cdp))
            .background(AppColorsProvider.current.card)
            .padding(vertical = 80.cdp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = tip,
            color = AppColorsProvider.current.secondText,
            fontSize = 28.csp,
            modifier = Modifier.padding(bottom = 48.cdp)
        )

        Button(
            onClick = {},
            modifier = Modifier
                .padding(start = 160.cdp, end = 160.cdp, bottom = 40.cdp)
                .fillMaxWidth()
                .height(70.cdp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColorsProvider.current.primary
            ),
            content = {
                Text(text = "去添加", fontSize = 30.csp, color = Color.White)
            }
        )
    }
}