package com.arkivanov.compose3d.entities

import androidx.compose.ui.graphics.Color
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector
import com.arkivanov.compose3d.plus

data class Parallelepiped(
    val position: Vector,
    val length: Vector,
    val width: Vector,
    val height: Vector,
    val color: Color
) : Entity {

    private val r1 = Rectangle(position = position, length = length, width = width, color = color)
    private val r2 = Rectangle(position = position, length = length, width = height, color = color)
    private val r3 = Rectangle(position = position + width, length = length, width = height, color = color)
    private val r4 = Rectangle(position = position, length = width, width = height, color = color)
    private val r5 = Rectangle(position = position + length, length = width, width = height, color = color)
    private val r6 = Rectangle(position = position + height, length = length, width = width, color = color)

    override fun triangles(consumer: (Triangle) -> Unit) {
        r1.triangles(consumer)
        r2.triangles(consumer)
        r3.triangles(consumer)
        r4.triangles(consumer)
        r5.triangles(consumer)
        r6.triangles(consumer)
    }
}
