package io.slava0135.cashfinder.model

class Position(val x: Int, val y: Int)

class Node(val value: Int, val position: Position) {
    private val nodes = mutableListOf<Node>()
    fun getNodes() = nodes.toList()
    fun addNode(node: Node) {
        nodes.add(node)
    }
}

class Result(val cash: Int, val path: List<Node>)

fun solve(graph: List<Node>): Result {

}