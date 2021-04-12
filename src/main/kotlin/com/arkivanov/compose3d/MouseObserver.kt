package com.arkivanov.compose3d

import java.awt.event.MouseMotionListener
import javax.swing.SwingUtilities
import java.lang.Runnable
import kotlin.jvm.JvmStatic
import javax.swing.JFrame
import com.arkivanov.compose3d.MouseObserver
import java.awt.Component
import java.lang.IllegalArgumentException
import java.awt.event.ActionListener
import java.awt.MouseInfo
import java.awt.Point
import kotlin.jvm.Synchronized
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.util.HashSet
import javax.swing.Timer

class MouseObserver constructor(component: Component?) {
    val component: Component
    private val timer: Timer
    private val mouseMotionListeners: MutableSet<MouseMotionListener>
    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }

    fun addMouseMotionListener(listener: MouseMotionListener) {
        synchronized(mouseMotionListeners) { mouseMotionListeners.add(listener) }
    }

    fun removeMouseMotionListener(listener: MouseMotionListener) {
        synchronized(mouseMotionListeners) { mouseMotionListeners.remove(listener) }
    }

    protected fun fireMouseMotionEvent(point: Point) {
        synchronized(mouseMotionListeners) {
            for (listener: MouseMotionListener in mouseMotionListeners) {
                val event = MouseEvent(
                    component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                    0, point.x, point.y, 0, false
                )
                SwingUtilities.invokeLater(Runnable { listener.mouseMoved(event) })
            }
        }
    }

    companion object {
        /* the resolution of the mouse motion */
        private const val DELAY = 10

        /* Testing the ovserver */
        @JvmStatic
        fun main(args: Array<String>) {
            val main = JFrame("dummy...")
            main.setSize(100, 100)
            main.isVisible = true
            val mo = MouseObserver(main)
            mo.addMouseMotionListener(object : MouseMotionListener {
                override fun mouseMoved(e: MouseEvent) {
                    println("mouse moved: " + e.point)
                }

                override fun mouseDragged(e: MouseEvent) {
                    println("mouse dragged: " + e.point)
                }
            })
            mo.start()
        }
    }

    init {
        requireNotNull(component) { "Null component not allowed." }
        this.component = component

        /* poll mouse coordinates at the given rate */timer = Timer(DELAY, object : ActionListener {
            private var lastPoint = MouseInfo.getPointerInfo().location

            /* called every DELAY milliseconds to fetch the
                 * current mouse coordinates */
            @Synchronized
            override fun actionPerformed(e: ActionEvent) {
                val point = MouseInfo.getPointerInfo().location
                if (point != lastPoint) {
                    fireMouseMotionEvent(point)
                }
                lastPoint = point
            }
        })
        mouseMotionListeners = HashSet()
    }
}
