package io.slava0135.cashfinder.model.solvedgraph

import io.slava0135.cashfinder.model.Solution
import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.graph.Node
import io.slava0135.cashfinder.model.graph.Position

class SolvedGraph private constructor(val width: Int, val height: Int) {

    val grid = Array(width) { Array(height) { SolvedNode(0) } }
    val walls = Array(width) { Array(height) { SolvedWall() } }

    lateinit var solution: Solution

    companion object Factory {

        fun createEmpty(width: Int, height: Int): SolvedGraph = SolvedGraph(width, height)

        fun createFromSolution(graph: Graph, solution: Solution): SolvedGraph {

            val solvedGraph = SolvedGraph(graph.width, graph.height).apply { this.solution = solution }

            val walls = solvedGraph.walls
            val grid = solvedGraph.grid
            val width = graph.width
            val height = graph.height

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

            return solvedGraph
        }

        private val rowRegex = Regex("""([+]([-#]+|\s+))+[+]""") // +--+-+###+
        private val colRegex = Regex("""([+]([|#]|\s))+[+]""") // +#+|+#+

        fun createFromLines(readLines: List<String>): SolvedGraph {
            TODO()
        }
    }

    init {

    }

    override fun toString(): String {

        val spaces = IntArray(width)
        for (y in 0 until height) {
            for (x in grid.indices) {
                val len = grid[x][y].value.toString().length
                spaces[x] = Integer.max(spaces[x], len)
            }
        }

        val result = mutableListOf<String>()
        if (solution.score != null) {
            result.add("${solution.initial};${solution.score}")
        } else {
            result.add("No path")
        }

        val border = "+" + spaces.joinToString("+") { "-".repeat(it) } + "+"
        result.add(border)

        for (y in 0 until height) {
            val line = StringBuilder("|")
            val lowerLine = StringBuilder("+")

            val iterator = spaces.iterator()
            var space = iterator.next()

            for (x in 0 until width) {
                when {
                    grid[x][y].isStart -> line.append("S".padStart(space))
                    grid[x][y].isEnd -> line.append("F".padStart(space))
                    else -> line.append(grid[x][y].value.toString().padStart(space))
                }

                when (walls[x][y].right) {
                    SolvedWall.WallState.WALL -> line.append('|')
                    SolvedWall.WallState.NO_WALL -> line.append(' ')
                    SolvedWall.WallState.ON_PATH -> line.append('#')
                }

                when (walls[x][y].down) {
                    SolvedWall.WallState.WALL -> lowerLine.append("-".repeat(space))
                    SolvedWall.WallState.NO_WALL -> lowerLine.append(" ".repeat(space))
                    SolvedWall.WallState.ON_PATH -> lowerLine.append("#".repeat(space))
                }
                lowerLine.append("+")

                if (iterator.hasNext()) {
                    space = iterator.next()
                } else break
            }
            result.add(line.toString())
            result.add(lowerLine.toString())
        }
        result[result.lastIndex] = border
        return result.joinToString { "\n" }
    }
}