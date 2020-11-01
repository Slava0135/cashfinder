package io.slava0135.cashfinder.model.graph

data class Position(val x: Int, val y: Int)

class Node(var value: Int, val position: Position) {

    val others = mutableListOf<Node>()

    var isEnd = false
    var isStart = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (value != other.value) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value
        result = 31 * result + position.hashCode()
        return result
    }

    override fun toString(): String {
        return "$value in x:${position.x} y:${position.y}"
    }
}