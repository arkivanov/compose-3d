package com.arkivanov.compose3d.entities

import androidx.compose.ui.graphics.Color
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector
import com.arkivanov.compose3d.plus

data class Rectangle(
    val position: Vector,
    val length: Vector,
    val width: Vector,
    val color: Color
) : Entity {

    private val t1: Triangle
    private val t2: Triangle

    init {
        val a = position
        val b = position + length
        val c = position + width
        val d = c + length
        t1 = Triangle(a = a, b = b, c = c, color = color)
        t2 = Triangle(a = b, b = c, c = d, color = color)
    }

    override fun triangles(consumer: (Triangle) -> Unit) {
        consumer(t1)
        consumer(t2)
    }
}
