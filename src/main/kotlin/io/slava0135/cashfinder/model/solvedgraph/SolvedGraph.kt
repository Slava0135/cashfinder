package io.slava0135.cashfinder.model.solvedgraph

import io.slava0135.cashfinder.model.Solution
import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.graph.Node
import io.slava0135.cashfinder.model.graph.Position

class SolvedGraph(val width: Int, val height: Int) {

    val grid = Array(width) { Array(height) { SolvedNode(0) } }
    val walls = Array(width) { Array(height) { SolvedWall() } }

    lateinit var solution: Solution

    companion object Factory {

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

        fun createFromLines(rawLines: List<String>): SolvedGraph {

            require(rawLines.isNotEmpty())
            val data = rawLines.first().split(" ")
            //val solution = Solution(emptyList(), data.first().toInt(), data.lastOrNull()?.toInt())

            val lines = rawLines.drop(1)

            require(lines.size > 2 && lines[1].length > 2)

            //checking lines lengths
            if (lines.any { it.length != lines.first().length })
                throw IllegalArgumentException("Different lines lengths")

            //checking rows
            if (lines.first().any { it !in "+-" } || lines.last().any { it !in "+-" })
                throw IllegalArgumentException("Bad upper/bottom border")
            for (row in lines.indices) {
                if (row % 2 == 0) {
                    if (rowRegex.matches(lines[row]) || lines[row].replace(" ", "-") != lines.first())
                        throw IllegalArgumentException("Row #$row is wrong")
                }
            }

            //reading crosses positions
            val crosses = mutableSetOf<Int>()
            for (index in lines[0].indices) {
                if (lines[0][index] == '+') {
                    crosses.add(index)
                }
            }

            //checking columns
            fun rotate(index: Int) = lines.map { it[index] }.joinToString("")
            val firstCol = rotate(0)
            if (firstCol.any { it !in "+|" } || rotate(lines[0].length - 1).any { it !in "+|" })
                throw IllegalArgumentException("Bad left/right border")
            for (col in lines[0].indices) {
                val line = rotate(col)
                if (col in crosses) {
                    if (colRegex.matches(line))
                        throw IllegalArgumentException("Column #$col is wrong")
                }
            }

            var startFound = false
            var endFound = false

            val len = lines[0].length
            val graph = SolvedGraph(crosses.size - 1, lines.size / 2)
            var x = 0
            //only rows with digits
            for ((y, row) in (1 until lines.size step 2).withIndex()) {

                val iterator = crosses.drop(1).iterator()
                var cross = iterator.next()

                val buffer = StringBuilder()

                var col = 1
                while (col < len) {
                    if (col == cross) {
                        //not including most left and right walls
                        if (col < len - 1) {
                            when(lines[row][col]) {
                                '|' -> {
                                    graph.walls[x + 1][y].left = SolvedWall.WallState.WALL
                                    graph.walls[x][y].right = SolvedWall.WallState.WALL
                                }
                                '#' -> {
                                    graph.walls[x + 1][y].left = SolvedWall.WallState.ON_PATH
                                    graph.walls[x][y].right = SolvedWall.WallState.ON_PATH
                                }
                            }
                        }

                        val value = buffer.replace(Regex("""\s"""), "")
                        when {
                            value.toIntOrNull() != null -> graph.grid[x][y] = SolvedNode(value.toInt())
                            value == "S" -> {
                                if (startFound) throw IllegalArgumentException("Multiple start tiles")
                                graph.grid[x][y] = SolvedNode(0).apply { isStart = true }
                                startFound = true
                            }
                            value == "F" -> {
                                if (endFound) throw IllegalArgumentException("Multiple finish tiles")
                                graph.grid[x][y] = SolvedNode(0).apply { isEnd = true }
                                endFound = true
                            }
                            else -> throw IllegalArgumentException("Invalid tile x:$x y:$y")
                        }
                        buffer.clear()

                        if (iterator.hasNext()) {
                            cross = iterator.next()
                        } else break
                        x++

                    } else {
                        if (buffer.isEmpty()) {
                            graph.walls[x][y].up = when (lines[row - 1][col]) {
                                '-' -> SolvedWall.WallState.WALL
                                '#' -> SolvedWall.WallState.ON_PATH
                                else -> SolvedWall.WallState.NO_WALL
                            }
                            graph.walls[x][y].down = when (lines[row + 1][col]) {
                                '-' -> SolvedWall.WallState.WALL
                                '#' -> SolvedWall.WallState.ON_PATH
                                else -> SolvedWall.WallState.NO_WALL
                            }
                        }
                        buffer.append(lines[row][col])
                    }
                    col++
                }
                x = 0
            }
            generateOuterWalls(graph)
            return graph
        }

        private fun generateOuterWalls(graph: SolvedGraph) {
            for (i in 0 until graph.height) {
                graph.walls[0][i].left = SolvedWall.WallState.WALL
                graph.walls[graph.width - 1][i].right = SolvedWall.WallState.WALL
            }
            for (i in 0 until graph.width) {
                graph.walls[i][0].up = SolvedWall.WallState.WALL
                graph.walls[i][graph.height - 1].down = SolvedWall.WallState.WALL
            }
        }

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
            result.add("${solution.initial}")
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