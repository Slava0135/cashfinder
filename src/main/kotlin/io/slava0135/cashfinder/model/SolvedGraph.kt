package io.slava0135.cashfinder.model

class SolvedNode(val value: Int) {
    var isOnPath = false
    var isEnd = false
    var isStart = false
}

class SolvedGraph(graph: Graph) {
    val grid = Array(graph.width) { Array(graph.height) { SolvedNode(0) } }
    val walls = graph.walls.clone()

    init {
        grid[graph.start!!.position.x][graph.start!!.position.y].isStart = true
        grid[graph.end!!.position.x][graph.end!!.position.y].isEnd = true
    }
}