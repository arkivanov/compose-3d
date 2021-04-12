package com.arkivanov.compose3d.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.compose3d.Camera

@Composable
fun Canvas3D(camera: Camera, modifier: Modifier, onDraw: Draw3DScope.() -> Unit) {
    Canvas(modifier) {
        Draw3DScopeImpl(
            camera = camera,
            drawScope = this
        ).onDraw()
    }
}
