package com.ssk.ncmusic.ui.common

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Created by ssk on 2022/4/21.
 */

@Stable
class CommonHeadBackgroundShape(var radius: Float = 80f) : Shape {

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