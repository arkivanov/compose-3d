package com.arkivanov.compose3d.draw

import androidx.compose.ui.graphics.Color
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector

interface Draw3DScope {

    fun drawLine(start: Vector, end: Vector, color: Color)

    fun drawTriangle(triangle: Triangle)
}
