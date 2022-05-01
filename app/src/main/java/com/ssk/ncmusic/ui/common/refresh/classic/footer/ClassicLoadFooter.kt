package com.ssk.ncmusic.ui.common.refresh.classic.footer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ssk.ncmusic.ui.common.refresh.classic.ProgressDrawable

/**
 * Created by ssk on 2022/1/10.
 * Description-> 经典上拉加载更多footer
 */
@Composable
fun ClassicLoadFooter() {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val (ivLoading, tvStatus) = createRefs()
        createHorizontalChain(ivLoading, tvStatus, chainStyle = ChainStyle.Packed)

        Image(
            painter = rememberDrawablePainter(ProgressDrawable().apply {
                setColor(0xff666666.toInt())
            }),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(ivLoading) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(tvStatus.start)
                }
                .padding(end = 10.dp)
                .size(20.dp)
        )

        Text(
            text = "正在加载...",
            fontSize = 16.sp,
            color = Color(0xff666666),
            modifier = Modifier.constrainAs(tvStatus) {
                end.linkTo(parent.end)
                start.linkTo(ivLoading.start)
                centerVerticallyTo(parent)
            }
        )
    }
}
