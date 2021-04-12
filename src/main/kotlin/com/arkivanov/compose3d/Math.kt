package com.arkivanov.compose3d

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

val UNIT_O_X = Vector(x = 1.0)
val UNIT_O_Y = Vector(y = 1.0)
val UNIT_O_Z = Vector(z = 1.0)

fun Vector.length(): Double = sqrt(x * x + y * y + z * z)

fun Vector.normalize(): Vector {
    val len = length().takeUnless { it == 0.0 } ?: return this

    return copy(x = x / len, y = y / len, z = z / len)
}

fun Vector.offset(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): Vector =
    copy(x = this.x + x, y = this.y + y, z = this.z + z)

operator fun Vector.plus(other: Vector): Vector =
    offset(x = other.x, y = other.y, z = other.z)

operator fun Vector.minus(other: Vector): Vector =
    offset(x = -other.x, y = -other.y, z = -other.z)

operator fun Vector.unaryMinus(): Vector =
    Vector(x = -x, y = -y, z = -z)

operator fun Vector.times(other: Double): Vector =
    Vector(x = x * other, y = y * other, z = z * other)

infix fun Vector.dotProduct(other: Vector): Double =
    x * other.x + y * other.y + z * other.z

fun Vector.rotate(origin: Vector, angle: Double): Vector {
    val c = cos(angle)
    val s = sin(angle)

    return multiplyByMatrix(
        v11 = c + (1 - c) * origin.x * origin.x,
        v12 = (1 - c) * origin.x * origin.y - s * origin.z,
        v13 = (1 - c) * origin.x * origin.z + s * origin.y,
        v21 = (1 - c) * origin.y * origin.x + s * origin.z,
        v22 = c + (1 - c) * origin.y * origin.y,
        v23 = (1 - c) * origin.y * origin.z - s * origin.x,
        v31 = (1 - c) * origin.z * origin.x - s * origin.y,
        v32 = (1 - c) * origin.z * origin.y + s * origin.x,
        v33 = c + (1 - c) * origin.z * origin.z
    )
}

fun Vector.rotate(orientation: Orientation): Vector =
    rotate(UNIT_O_X, orientation.x)
        .rotate(UNIT_O_Y, orientation.y)
        .rotate(UNIT_O_Z, orientation.z)

fun angleBetween(a: Vector, b: Vector): Double =
    acos((a dotProduct b) / (a.length() * b.length()))

fun distanceBetween(a: Vector, b: Vector): Double =
    sqrt(sqr(a.x - b.x) + sqr(a.y - b.y) + sqr(a.z - b.z))

fun Vector.multiplyByMatrix(
    v11: Double,
    v12: Double,
    v13: Double,
    v21: Double,
    v22: Double,
    v23: Double,
    v31: Double,
    v32: Double,
    v33: Double,
): Vector =
    Vector(
        x = v11 * x + v12 * y + v13 * z,
        y = v21 * x + v22 * y + v23 * z,
        z = v31 * x + v32 * y + v33 * z
    )

operator fun Triangle.plus(vector: Vector): Triangle =
    copy(a = a + vector, b = b + vector, c = c + vector)

fun degToRad(deg: Double): Double = deg * PI / 180.0

fun Double.fixZero(): Double = if (this == 0.0) 0.001 else this

fun sqr(value: Double): Double = value * value
