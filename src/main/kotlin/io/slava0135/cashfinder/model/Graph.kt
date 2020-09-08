package io.slava0135.cashfinder.model

class Position(val x: Int, val y: Int)

class Node(val value: Int, val position: Position) {
    private val nodes = mutableListOf<Node>()
    fun getNodes() = nodes.toList()
    fun addNode(node: Node) {
        nodes.add(node)
    }
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

    fun getLinkedNodes(): List<Node> {
        TODO()
    }
}