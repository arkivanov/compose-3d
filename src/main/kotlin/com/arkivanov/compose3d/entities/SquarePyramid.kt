package com.arkivanov.compose3d.entities

import androidx.compose.ui.graphics.Color
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector
import com.arkivanov.compose3d.plus
import com.arkivanov.compose3d.times

data class SquarePyramid(
    val position: Vector,
    val length: Vector,
    val width: Vector,
    val height: Vector,
    val color: Color
) : Entity {

    private val t1: Triangle
    private val t2: Triangle
    private val t3: Triangle
    private val t4: Triangle
    private val t5: Triangle
    private val t6: Triangle

    init {
        val a = position
        val b = position + length
        val c = position + width
        val d = c + length
        t1 = Triangle(a = a, b = b, c = c, color = color)
        t2 = Triangle(a = b, b = c, c = d, color = color)
        val o = position + length * 0.5 + width * 0.5
        val h = o + height
        t3 = Triangle(a = a, b = b, c = h, color = color)
        t4 = Triangle(a = a, b = c, c = h, color = color)
        t5 = Triangle(a = c, b = d, c = h, color = color)
        t6 = Triangle(a = b, b = d, c = h, color = color)
    }

    override fun triangles(consumer: (Triangle) -> Unit) {
        consumer(t1)
        consumer(t2)
        consumer(t3)
        consumer(t4)
        consumer(t5)
        consumer(t6)
    }
}
