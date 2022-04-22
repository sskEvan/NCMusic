package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.common.TableLayout
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun CpnMusicApplication() {
    val musicApplications = remember {
        listOf(
            MusicApplicationBean(R.drawable.ic_music, "本地/下载"),
            MusicApplicationBean(R.drawable.ic_cloud_disk, "云盘"),
            MusicApplicationBean(R.drawable.ic_buy, "已购"),
            MusicApplicationBean(R.drawable.ic_recent_play, "最近播放"),
            MusicApplicationBean(R.drawable.ic_friends, "我的好友"),
            MusicApplicationBean(R.drawable.ic_collect, "收藏和赞"),
            MusicApplicationBean(R.drawable.ic_podcast, "我的播客"),
            MusicApplicationBean(R.drawable.ic_rock, "摇滚专区")
        )
    }

    TableLayout(cellsCount = 4) {
        musicApplications.forEachIndexed { _, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                    }
            ) {
                Icon(
                    painterResource(item.imageResId),
                    null,
                    tint = AppColorsProvider.current.primary,
                    modifier = Modifier
                        .padding(top = 20.cdp)
                        .size(60.cdp)
                )
                Text(
                    text = item.name,
                    modifier = Modifier.padding(vertical = 20.cdp),
                    color = AppColorsProvider.current.secondText,
                    fontSize = 24.csp
                )
            }
        }
    }
}

data class MusicApplicationBean(val imageResId: Int, val name: String)
