package com.ssk.ncmusic.ui.page.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.ui.common.CommonHeadBackgroundShape
import com.ssk.ncmusic.ui.common.CommonLocalImage
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.page.mine.component.CpnUserInfo
import com.ssk.ncmusic.ui.page.mine.mineCommonCard
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/4/20.
 */
@Composable
fun ProfilePage() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background)
    ) {

        CommonNetworkImage(
            url = AppGlobalData.sLoginResult?.profile?.backgroundUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(584.cdp)
                .clip(CommonHeadBackgroundShape()),
            error = R.drawable.ic_bg,
            contentScale = ContentScale.FillBounds
        )

        Column {
            // 用户信息
            CpnUserInfo(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 88.cdp)
                    .padding(top = 280.cdp)
            )

            Box(
                modifier = Modifier
                    .mineCommonCard()
                    .height(400.cdp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "个人详情页", fontSize = 50.csp, fontWeight = FontWeight.Bold)
            }
        }

    }

}