package io.slava0135.cashfinder.model

class SolvedNode(val value: Int) {
    var isOnPath = false
    var isEnd = false
    var isStart = false
}

class SolvedWall {
    var up = WallState.NO_WALL
    var right = WallState.NO_WALL
    var left = WallState.NO_WALL
    var down = WallState.NO_WALL

    enum class WallState {
        NO_WALL,
        WALL,
        ON_PATH
    }
}

class SolvedGraph(graph: Graph, val solution: Solution) {
    val width = graph.width
    val height = graph.height

    val grid = Array(width) { Array(height) { SolvedNode(0) } }
    val walls = Array(width) { Array(height) { SolvedWall() } }

    init {
        for (x in 0 until width) {
            for (y in 0 until height) {
                walls[x][y].apply {
                    if (graph.walls[x][y].up) up = SolvedWall.WallState.WALL
                    if (graph.walls[x][y].down) down = SolvedWall.WallState.WALL
                    if (graph.walls[x][y].right) right = SolvedWall.WallState.WALL
                    if (graph.walls[x][y].left) left = SolvedWall.WallState.WALL
                }
                grid[x][y] = SolvedNode(graph.grid[x][y].value)
            }
        }

        grid[graph.start!!.position.x][graph.start!!.position.y].isStart = true
        grid[graph.end!!.position.x][graph.end!!.position.y].isEnd = true

        if (solution.nodes.isNotEmpty()) {
            val nodes = solution.nodes.iterator()
            var tail = nodes.next()
            grid[tail.position.x][tail.position.y].isOnPath = true
            var head: Node
            do {
                head = nodes.next()
                grid[head.position.x][head.position.y].isOnPath = true
                when {
                    tail.position.x > head.position.x -> {
                        walls[tail.position.x][tail.position.y].left = SolvedWall.WallState.ON_PATH
                        walls[head.position.x][head.position.y].right = SolvedWall.WallState.ON_PATH
                    }
                    tail.position.x < head.position.x -> {
                        walls[tail.position.x][tail.position.y].right = SolvedWall.WallState.ON_PATH
                        walls[head.position.x][head.position.y].left = SolvedWall.WallState.ON_PATH
                    }
                    tail.position.y > head.position.y -> {
                        walls[tail.position.x][tail.position.y].up = SolvedWall.WallState.ON_PATH
                        walls[head.position.x][head.position.y].down = SolvedWall.WallState.ON_PATH
                    }
                    tail.position.y < head.position.y -> {
                        walls[tail.position.x][tail.position.y].down = SolvedWall.WallState.ON_PATH
                        walls[head.position.x][head.position.y].up = SolvedWall.WallState.ON_PATH
                    }
                }
                tail = head
            } while (nodes.hasNext())
        }
    }
}