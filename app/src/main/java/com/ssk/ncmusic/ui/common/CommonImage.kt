package com.ssk.ncmusic.ui.common

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.theme.AppColorsProvider

/**
 * Created by ssk on 2022/4/18.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun CommonNetworkImage(
    url: Any?,
    placeholder: Int = R.drawable.ic_default_place_holder,
    error: Int = R.drawable.ic_default_place_holder,
    allowHardware: Boolean = false,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null
) {
    Image(
        rememberImagePainter(url,
            builder = {
                if(placeholder != -1) {
                    placeholder(placeholder)
                }
                if(error != -1) {
                    error(error)
                }
                allowHardware(allowHardware)
            }
        ),
        contentDescription = "头像",
        contentScale = ContentScale.Crop,
        modifier = modifier,
        colorFilter = colorFilter
    )
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun CommonLocalImage(
    resId: Int,
    allowHardware: Boolean = false,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null
) {

    Image(
        rememberImagePainter(resId,
            builder = {
                allowHardware(allowHardware)
            }
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        colorFilter = colorFilter
    )
}

@Composable
fun CommonIcon(
    resId: Int,
    modifier: Modifier = Modifier,
    tint: Color = AppColorsProvider.current.firstIcon
) {

    Icon(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}