package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick

/**
 * Created by ssk on 2022/4/23.
 */
@Composable
fun CpnSongItem(index: Int, songBean: SongBean, onClick: (index: Int) -> Unit) {
    Row(
        Modifier.onClick {
            onClick.invoke(index)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (MusicPlayController.isPlaying(songBean)) {
            CpnPlayingMark(playing = MusicPlayController.isPlaying(), modifier = Modifier.width(120.cdp))
        } else {
            Text(
                text = (index + 1).toString(),
                fontSize = 30.csp,
                color = AppColorsProvider.current.secondText,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(120.cdp)
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 26.cdp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = songBean.name,
                fontSize = 32.csp,
                color = AppColorsProvider.current.firstText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = songBean.ar[0].name,
                fontSize = 24.csp,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier.padding(top = 10.cdp)
            )
        }

        CommonIcon(
            resId = R.drawable.ic_sheet_menu,
            modifier = Modifier
                .height(32.cdp)
                .padding(horizontal = 32.cdp)
        )
    }
}