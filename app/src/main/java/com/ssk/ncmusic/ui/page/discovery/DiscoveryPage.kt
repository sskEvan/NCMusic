package com.ssk.ncmusic.ui.page.discovery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.ui.common.CommonTopAppBar

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun DiscoveryPage() {
    Column(Modifier.statusBarsPadding().fillMaxSize()) {
        CommonTopAppBar(title = "发现", leftIconResId = -1)
        Text("发现")
    }
}