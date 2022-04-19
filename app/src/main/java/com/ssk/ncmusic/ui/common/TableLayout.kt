package com.ssk.ncmusic.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun TableLayout(
    modifier: Modifier = Modifier,
    cellsCount: Int,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val parentWidth = constraints.maxWidth
        val cellWidth = parentWidth / cellsCount
        var totalHeight = 0
        val cellsHeightPerRow = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()
        val placeables = measurables.mapIndexed { index, measurable ->
            val newConstraints = constraints.copy(minWidth = cellWidth, maxWidth = cellWidth)
            val placeable = measurable.measure(newConstraints)
            val childWidth = placeable.width
            val childHeight = placeable.height
            cellsHeightPerRow.add(childHeight)
            if (cellsHeightPerRow.size == cellsCount || index == measurables.size - 1) {
                var maxChildHeight = 0
                cellsHeightPerRow.forEach {
                    if (it > maxChildHeight)
                        maxChildHeight = it
                }
                totalHeight += maxChildHeight
                rowHeights.add(maxChildHeight)
                cellsHeightPerRow.clear()
            }
            placeable
        }
        layout(parentWidth, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val column = index % cellsCount
                val row = index / cellsCount
                val positionX = cellWidth * column
                var positionY = 0
                for (i in 0 until row) {
                    positionY += rowHeights[i]
                }
                placeable.placeRelative(positionX, positionY)
            }
        }
    }
}