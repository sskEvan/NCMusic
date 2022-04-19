package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssk.ncmusic.R
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.toPx




/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun UserPlaylistItem(platListBean: PlaylistBean?) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .clickable {

            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        platListBean?.let {
            CommonNetworkImage(
                url = it.coverImgUrl,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(5.dp)),
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = it.name,
                    fontSize = 14.sp,
                    color = AppColorsProvider.current.firstText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "共${it.trackCount}首",
                    fontSize = 12.sp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_sheet_menu),
                contentDescription = "",
                modifier = Modifier
                    .height(15.dp)
            )
        }
    }


}