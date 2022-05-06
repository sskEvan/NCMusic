package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun CpnUserInfo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        val loginResult = AppGlobalData.sLoginResult
        Column(
            modifier = Modifier
                .padding(top = 60.cdp, start = 32.cdp, end = 32.cdp)
                .fillMaxWidth()
                .height(240.cdp)
                .clip(RoundedCornerShape(24.cdp))
                .background(AppColorsProvider.current.card),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = loginResult.profile.nickname,
                fontSize = 40.csp,
                color = AppColorsProvider.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 64.cdp)
            )
            Text(
                text = "${loginResult.profile.follows} 关注  " +
                        "｜  ${loginResult.profile.followeds} 粉丝",
                fontSize = 32.csp,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier.padding(top = 36.cdp)
            )
        }

        CommonNetworkImage(
            url = loginResult.profile.avatarUrl,
            placeholder = R.drawable.ic_default_avator,
            error = R.drawable.ic_default_avator,
            modifier = Modifier
                .size(120.cdp)
                .clip(
                    RoundedCornerShape(50)
                )
        )
    }
}

