package io.slava0135.cashfinder.model

class Position(val x: Int, val y: Int)

class Node(val value: Int, val position: Position) {
    val nodes = mutableListOf<Node>()
}

class Wall {
    var left = false
    var right = false
    var up = false
    var down = false
}

class Graph(val width: Int, val height: Int) {
    val grid = Array(width) { Array<Node?>(height) { null } }
    val walls = Array(width) { Array<Node?>(height) { null } }

    val start: Node? = null
    val end: Node? = null

    override fun toString(): String {
        TODO()
    }
}