package com.ssk.ncmusic.ui.page.mine

import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterKV
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp


/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun CpnUserPlayListItem(platListBean: PlaylistBean?) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                val json = Uri.encode(Gson().toJson(platListBean))
                NCNavController.instance.navigate("${RouterUrls.PLAY_LIST}/$json")
            }
            .padding(start = 32.cdp, end = 32.cdp, top = 8.cdp, bottom = 8.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        platListBean?.let {
            CommonNetworkImage(
                url = it.coverImgUrl,
                modifier = Modifier
                    .padding(end = 20.cdp)
                    .size(110.cdp)
                    .clip(RoundedCornerShape(10.cdp)),
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = it.name,
                    fontSize = 30.csp,
                    color = AppColorsProvider.current.firstText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "共${it.trackCount}首",
                    fontSize = 24.csp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_sheet_menu),
                contentDescription = "",
                modifier = Modifier
                    .height(30.cdp)
            )
        }
    }


}









