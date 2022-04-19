package com.ssk.ncmusic.ui.page.sing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.THEME_BLUE
import com.ssk.ncmusic.ui.theme.THEME_DEFAULT
import com.ssk.ncmusic.ui.theme.themeTypeState

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun SingPage() {
    Column(Modifier.fillMaxSize()) {
        CommonTopAppBar(title = "关注", modifier = Modifier.statusBarsPadding())
        Text("关注")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "默认主题", modifier = Modifier
                .width(100.dp)
                .clickable {
                    themeTypeState.value = THEME_DEFAULT
                })
            Text(text = "蓝色主题", modifier = Modifier
                .width(100.dp)
                .clickable {
                    themeTypeState.value = THEME_BLUE
                })
        }
    }
}