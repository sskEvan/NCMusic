package com.ssk.ncmusic.ui.page.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/13.
 */
@Composable
fun CpnHomeDrawer(drawerState: DrawerState) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
    ) {
        UserInfoComponent(drawerState)
        SettingComponent()
    }
}

@Composable
private fun UserInfoComponent(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 24.cdp)
            .fillMaxWidth()
            .height(100.cdp)
            .onClick {
                scope.launch {
                    drawerState.close()
                    NCNavController.instance.navigate(RouterUrls.PROFILE)
                }
            }
            .padding(horizontal = 32.cdp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommonNetworkImage(
            url = AppGlobalData.sLoginResult.profile.avatarUrl,
            placeholder = R.drawable.ic_default_avator,
            error = R.drawable.ic_default_avator,
            modifier = Modifier
                .size(60.cdp)
                .clip(
                    RoundedCornerShape(60)
                )
        )
        Text(
            text = AppGlobalData.sLoginResult.profile.nickname,
            fontSize = 36.csp,
            color = AppColorsProvider.current.firstText,
            modifier = Modifier.padding(start = 20.cdp)
        )

        CommonIcon(
            resId = R.drawable.ic_arrow_right,
            modifier = Modifier
                .padding(8.cdp)
                .size(30.cdp)
        )
    }
}

@Composable
private fun SettingComponent() {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.cdp, vertical = 16.cdp)
            .background(AppColorsProvider.current.card, RoundedCornerShape(24.cdp))
    ) {
        Text(
            text = "设置",
            modifier = Modifier.padding(horizontal = 32.cdp, vertical = 24.cdp),
            fontSize = 28.csp,
            color = AppColorsProvider.current.secondText
        )
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.cdp,
            color = AppColorsProvider.current.divider
        )

        CpnDrawerThemeSetting()
    }
}
