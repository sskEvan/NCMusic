package com.ssk.ncmusic.ui.page.mine

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.transformDp

/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun UserInfoComponent(modifier: Modifier = Modifier) {
    Log.d("ssk", "screen width dp = ${Resources.getSystem().displayMetrics.widthPixels.transformDp}")
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier
                .padding(top = 60.cdp, bottom = 12.cdp, start = 32.cdp, end = 32.cdp)
                .fillMaxWidth()
                .height(240.cdp)
                .clip(RoundedCornerShape(24.cdp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ssk_evan",
                fontSize = 40.csp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 60.cdp)
            )
            Text(
                text = "2 关注  ｜  2 粉丝  ｜  Lv.8",
                fontSize = 32.csp,
                modifier = Modifier.padding(top = 20.cdp)
            )
        }

        CommonNetworkImage(
            url = AppGlobalData.sLoginResult.profile.avatarUrl,
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

@Stable
class BgImageShapes(var radius: Float = 80f) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, size.height - radius)
        path.quadraticBezierTo(size.width / 2f, size.height, size.width, size.height - radius)
        path.lineTo(size.width, 0f)
        path.close()
        return Outline.Generic(path)
    }
}