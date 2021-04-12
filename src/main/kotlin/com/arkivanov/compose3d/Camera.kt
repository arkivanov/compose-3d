package com.arkivanov.compose3d

data class Camera(
    val position: Vector,
    val orientation: Orientation,
    val surfacePosition: Vector
)
