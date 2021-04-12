package com.arkivanov.compose3d

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.arkivanov.compose3d.draw.Canvas3D
import com.arkivanov.compose3d.entities.Entity
import com.arkivanov.compose3d.entities.Parallelepiped
import com.arkivanov.compose3d.entities.Sphere
import com.arkivanov.compose3d.entities.SquarePyramid
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.Comparator
import java.util.PriorityQueue
import java.util.Queue
import kotlin.math.tan

fun main() {
    val focalLength = getFocalLength(size = 2000.0, fov = degToRad(90.0))

    val initialCameraPosition = Vector(y = 700.0, z = 550.0)
    val initialCameraOrientation = Orientation(x = degToRad(-35.0))

    val cube =
        Parallelepiped(
            position = Vector(x = -100.0, z = -100.0),
            length = Vector(z = 200.0),
            width = Vector(x = 200.0),
            height = Vector(y = 100.0),
            color = Color.Magenta
        )

    val pyramid =
        SquarePyramid(
            position = Vector(x = -100.0, y = 100.0, z = -100.0),
            length = Vector(z = 200.0),
            width = Vector(x = 200.0),
            height = Vector(y = 100.0),
            color = Color.Magenta
        )

    val sphere1 =
        Sphere(
            position = Vector(),
            radius = 200.0,
            color = Color.Magenta,
            n = 3
        )

    val sphere2 =
        Sphere(
            position = Vector(y = 300.0),
            radius = 100.0,
            color = Color.Magenta,
            n = 2
        )

    val sphere3 =
        Sphere(
            position = Vector(y = 450.0),
            radius = 50.0,
            color = Color.Magenta,
            n = 1
        )

    val nose =
        SquarePyramid(
            position = Vector(y = 450.0, z = 50.0),
            length = Vector(x = 5.0),
            width = Vector(y = 5.0),
            height = Vector(z = 30.0),
            color = Color.Red
        )

    val hand1 =
        Parallelepiped(
            position = Vector(x = 75.0, y = 360.0),
            length = Vector(x = 5.0),
            width = Vector(z = 5.0),
            height = Vector(y = -200.0),
            color = Color.DarkGray
        ).run {
            copy(
                length = length.rotate(Vector(z = 1.0), degToRad(35.0)),
                width = width.rotate(Vector(z = 1.0), degToRad(35.0)),
                height = height.rotate(Vector(z = 1.0), degToRad(35.0)),
            )
        }

    val hand2 =
        Parallelepiped(
            position = Vector(x = -80.0, y = 360.0),
            length = Vector(x = 5.0),
            width = Vector(z = 5.0),
            height = Vector(y = -200.0),
            color = Color.DarkGray
        ).run {
            copy(
                length = length.rotate(Vector(z = 1.0), degToRad(-35.0)),
                width = width.rotate(Vector(z = 1.0), degToRad(-35.0)),
                height = height.rotate(Vector(z = 1.0), degToRad(-35.0)),
            )
        }

//    val hand1 =
//        SquarePyramid(
//            position = Vector(),
//            length = Vector(x = 10.0),
//            width = Vector(z = 10.0),
//            height = Vector(y = -400.0),
//            color = Color.DarkGray
//        )

    Window(
        size = IntSize(width = 1000, height = 1000),
        title = "Compose-3D"
    ) {
        var camera by remember {
            mutableStateOf(
                Camera(
                    position = initialCameraPosition,
                    orientation = initialCameraOrientation,
                    surfacePosition = Vector(z = 1.0) * focalLength
                )
            )
        }

        fun move(block: Camera.() -> Camera) {
            camera = camera.block()
        }

        val w = LocalAppWindow.current

        val obs = remember {
            MouseObserver(w.window)
        }
        DisposableEffect(Unit) {
            obs.addMouseMotionListener(object : MouseMotionListener {
                private var lastPos: IntOffset? = null

                override fun mouseDragged(e: MouseEvent?) {
                }

                override fun mouseMoved(e: MouseEvent) {
                    val pos = lastPos
                    lastPos = IntOffset(e.xOnScreen, e.yOnScreen)
                    if (!w.window.isFocused) {
                        return
                    }

                    if (pos != null) {
                        val dx = e.xOnScreen - pos.x
                        val dy = e.yOnScreen - pos.y
                        move {
                            copy(
                                orientation = Orientation(
                                    x = orientation.x - degToRad(dy.toDouble()),
                                    y = orientation.y + degToRad(dx.toDouble()),
                                )
                            )
                        }
                    }
                }
            })
            obs.start()
            onDispose { obs.stop() }
        }

        val o = Vector(x = 0.0, y = 0.0, z = 0.0)
        val ox1 = Vector(x = 400.0, y = 0.0, z = 0.0)
        val ox2 = -ox1
        val oy1 = Vector(x = 0.0, y = 600.0, z = 0.0)
        val oy2 = -oy1
        val oz1 = Vector(x = 0.0, y = 0.0, z = 400.0)
        val oz2 = -oz1

        var deg = 0.0

//        LaunchedEffect(Unit) {
//            while (isActive) {
//                delay(32)
//                deg += degToRad(2.0)
//                if (deg >= 2 * PI) {
//                    deg -= 2 * PI
//                }
//
//                camera = camera.copy(
//                    position = initialCameraPosition.rotate(Vector(y = 1.0), deg),
//                    orientation = initialCameraOrientation.copy(y = deg)
//                )
//            }
//        }

        MaterialTheme {


            KeyListener(
                onForward = {
                    move { copy(position = position - UNIT_O_Z.rotate(orientation).copy(y = 0.0) * 10.0) }
                },
                onBackward = {
                    move { copy(position = position + UNIT_O_Z.rotate(orientation).copy(y = 0.0) * 10.0) }
                },
                onLeft = {
                    move { copy(position = position + UNIT_O_X.rotate(orientation).copy(y = 0.0) * 10.0) }
                },
                onRight = {
                    move { copy(position = position - UNIT_O_X.rotate(orientation).copy(y = 0.0) * 10.0) }
                },
                onUp = {
                    move { copy(position = position.offset(y = 10.0)) }
                },
                onDown = {
                    move { copy(position = position.offset(y = -10.0)) }
                },
            )

            val triangles =
                sortTriangles(
                    cameraPosition = camera.position,
                    sphere1,
                    sphere2,
                    sphere3,
                    nose,
                    hand1,
                    hand2
                )

            Canvas3D(camera = camera, modifier = Modifier.fillMaxSize()) {
                drawLine(start = o, end = ox1, color = Color.Red)
                drawLine(start = o, end = ox2, color = Color.Red)
                drawLine(start = o, end = oy1, color = Color.Green)
                drawLine(start = o, end = oy2, color = Color.Green)
                drawLine(start = o, end = oz1, color = Color.Blue)
                drawLine(start = o, end = oz2, color = Color.Blue)
//                cube.triangles(::drawTriangle)
//                pyramid.triangles(::drawTriangle)
//                sphere1.triangles(::drawTriangle)
//                sphere2.triangles(::drawTriangle)
//                sphere3.triangles(::drawTriangle)
//                nose.triangles(::drawTriangle)
//                hand1.triangles(::drawTriangle)
//                hand2.triangles(::drawTriangle)

                while (triangles.isNotEmpty()) {
                    drawTriangle(triangles.poll())
                }
            }
        }
    }
}

private fun sortTriangles(cameraPosition: Vector, vararg entities: Entity): Queue<Triangle> {
    val queue =
        PriorityQueue(
            Comparator.comparingDouble<Triangle> {
                distanceBetween(it.a, cameraPosition)
            }.reversed()
        )

    entities.forEach { entity ->
        entity.triangles {
            queue.offer(it)
        }
    }

    return queue
}

@Composable
private fun KeyListener(
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val pressedKeys = remember { HashSet<Key>() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (isActive) {
            if (Key.W in pressedKeys) {
                onForward()
            }

            if (Key.S in pressedKeys) {
                onBackward()
            }

            if (Key.A in pressedKeys) {
                onLeft()
            }

            if (Key.D in pressedKeys) {
                onRight()
            }

            if (Key.Spacebar in pressedKeys) {
                onUp()
            }

            if (Key.CtrlLeft in pressedKeys) {
                onDown()
            }

            delay(20)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusModifier()
            .onKeyEvent {
                when (it.nativeKeyEvent.id) {
                    NativeKeyEvent.KEY_PRESSED -> pressedKeys += it.key
                    NativeKeyEvent.KEY_RELEASED -> pressedKeys -= it.key
                }
                true
            }
    )
}

private fun getFocalLength(size: Double, fov: Double): Double =
    size / (2 * tan(fov / 2))

