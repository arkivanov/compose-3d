package com.arkivanov.compose3d.entities

import androidx.compose.ui.graphics.Color
import com.arkivanov.compose3d.Triangle
import com.arkivanov.compose3d.Vector
import com.arkivanov.compose3d.degToRad
import com.arkivanov.compose3d.length
import com.arkivanov.compose3d.plus
import com.arkivanov.compose3d.rotate
import com.arkivanov.compose3d.unaryMinus
import kotlin.math.atan

class Sphere(
    val position: Vector,
    val radius: Double,
    val color: Color,
    val n: Int
) : Entity {

    private val triangles: List<Triangle>

    // TODO: Optimize memory consumption here
    init {
        val top = Vector(y = radius)
        val bottom = -top

        val top1 = top.rotate(OX, VERTICAL_DELTA_RAD)
        val top2 = top1.rotate(OY, HORIZONTAL_DELTA_RAD)
        val top3 = top2.rotate(OY, HORIZONTAL_DELTA_RAD)
        val top4 = top3.rotate(OY, HORIZONTAL_DELTA_RAD)
        val top5 = top4.rotate(OY, HORIZONTAL_DELTA_RAD)

        val bottom1 = bottom.rotate(OX, -VERTICAL_DELTA_RAD).rotate(OY, HORIZONTAL_DELTA_RAD / 2.0)
        val bottom2 = bottom1.rotate(OY, HORIZONTAL_DELTA_RAD)
        val bottom3 = bottom2.rotate(OY, HORIZONTAL_DELTA_RAD)
        val bottom4 = bottom3.rotate(OY, HORIZONTAL_DELTA_RAD)
        val bottom5 = bottom4.rotate(OY, HORIZONTAL_DELTA_RAD)

        val queue = ArrayDeque<Triangle>()
        queue += Triangle(a = top, b = top1, c = top2, color = color)
        queue += Triangle(a = top, b = top2, c = top3, color = color)
        queue += Triangle(a = top, b = top3, c = top4, color = color)
        queue += Triangle(a = top, b = top4, c = top5, color = color)
        queue += Triangle(a = top, b = top5, c = top1, color = color)
        queue += Triangle(a = bottom, b = bottom1, c = bottom2, color = color)
        queue += Triangle(a = bottom, b = bottom2, c = bottom3, color = color)
        queue += Triangle(a = bottom, b = bottom3, c = bottom4, color = color)
        queue += Triangle(a = bottom, b = bottom4, c = bottom5, color = color)
        queue += Triangle(a = bottom, b = bottom5, c = bottom1, color = color)
        queue += Triangle(a = top1, b = bottom1, c = top2, color = color)
        queue += Triangle(a = bottom1, b = top2, c = bottom2, color = color)
        queue += Triangle(a = top2, b = bottom2, c = top3, color = color)
        queue += Triangle(a = bottom2, b = top3, c = bottom3, color = color)
        queue += Triangle(a = top3, b = bottom3, c = top4, color = color)
        queue += Triangle(a = bottom3, b = top4, c = bottom4, color = color)
        queue += Triangle(a = top4, b = bottom4, c = top5, color = color)
        queue += Triangle(a = bottom4, b = top5, c = bottom5, color = color)
        queue += Triangle(a = top5, b = bottom5, c = top1, color = color)
        queue += Triangle(a = bottom5, b = top1, c = bottom1, color = color)

        repeat(n) {
            repeat(queue.size) {
                queue.removeFirst().splitTo(queue)
            }
        }

        repeat(queue.size) {
            queue.addLast(queue.removeFirst() + position)
        }

        triangles = queue
    }

    private fun Triangle.splitTo(queue: ArrayDeque<Triangle>) {
        val ab = split(a, b).radius()
        val bc = split(b, c).radius()
        val ac = split(c, a).radius()
        queue.addLast(Triangle(a = a, b = ab, c = ac, color = color))
        queue.addLast(Triangle(a = b, b = ab, c = bc, color = color))
        queue.addLast(Triangle(a = c, b = ac, c = bc, color = color))
        queue.addLast(Triangle(a = ab, b = bc, c = ac, color = color))
    }

    private fun Vector.radius(): Vector {
        val len = length()

        return Vector(
            x = x * radius / len,
            y = y * radius / len,
            z = z * radius / len
        )
    }

    private fun split(a: Vector, b: Vector): Vector =
        Vector(
            x = (a.x + b.x) / 2.0,
            y = (a.y + b.y) / 2.0,
            z = (a.z + b.z) / 2.0
        )

    override fun triangles(consumer: (Triangle) -> Unit) {
        triangles.forEach(consumer)
    }

    private companion object {
        private val VERTICAL_DELTA_RAD = degToRad(90.0) - atan(0.5)
        private val HORIZONTAL_DELTA_RAD = degToRad(72.0)
        private val OX = Vector(x = 1.0)
        private val OY = Vector(y = 1.0)
    }
}
