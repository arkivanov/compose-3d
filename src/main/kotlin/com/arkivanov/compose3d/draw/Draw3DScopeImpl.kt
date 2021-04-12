package com.arkivanov.compose3d.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.arkivanov.compose3d.Camera
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector
import com.arkivanov.compose3d.distanceBetween
import com.arkivanov.compose3d.fixZero
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal class Draw3DScopeImpl(
    private val camera: Camera,
    drawScope: DrawScope
) : Draw3DScope, DrawScope by drawScope {

    override fun drawLine(start: Vector, end: Vector, color: Color) {
        drawLine(
            start = start.project(),
            end = end.project(),
            color = color
        )
    }

    override fun drawTriangle(triangle: Triangle) {
        val a = triangle.a.project()
        val b = triangle.b.project()
        val c = triangle.c.project()
        val f = lightFactor(triangle.a)

        val red = triangle.color.red + (1F - triangle.color.red) * f
        val green = triangle.color.green + (1F - triangle.color.green) * f
        val blue = triangle.color.blue + (1F - triangle.color.blue) * f

        drawPath(
            path = Path().apply {
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
                lineTo(c.x, c.y)
                lineTo(a.x, a.y)
            },
            color = Color(red = red, green = green, blue = blue),
        )
    }

    private fun lightFactor(point: Vector): Float =
        min(distanceBetween(camera.position, point).toFloat(), 2000F) / 2000F

    // Source: https://en.wikipedia.org/wiki/3D_projection#Mathematical_formula
    private fun Vector.project(): Offset {
        val x = x - camera.position.x
        val y = y - camera.position.y
        val z = z - camera.position.z

        val cx = cos(camera.orientation.x)
        val cy = cos(camera.orientation.y)
        val cz = cos(camera.orientation.z)
        val sx = sin(camera.orientation.x)
        val sy = sin(camera.orientation.y)
        val sz = sin(camera.orientation.z)

        val dx = cy * (sz * y + cz * x) - sy * z
        val dy = sx * (cy * z + sy * (sz * y + cz * x)) + cx * (cz * y - sz * x)
        val dz = cx * (cy * z + sy * (sz * y + cz * x)) - sx * (cz * y - sz * x)

        val bx = camera.surfacePosition.z * dx / dz.fixZero() + camera.surfacePosition.x
        val by = camera.surfacePosition.z * dy / dz.fixZero() + camera.surfacePosition.y

        return Offset(
            x = bx.toFloat() + 1000,
            y = by.toFloat() + 1000
        )
    }
}
