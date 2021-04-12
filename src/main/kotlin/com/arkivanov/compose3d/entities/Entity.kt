package com.arkivanov.compose3d.entities

import com.arkivanov.compose3d.Triangle

interface Entity {

    fun triangles(consumer: (Triangle) -> Unit)
}
