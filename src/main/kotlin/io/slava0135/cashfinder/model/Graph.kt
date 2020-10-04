package io.slava0135.cashfinder.model
import java.lang.Integer.max
import java.util.*

data class Position(val x: Int, val y: Int)

class Result(val cash: Int, val path: List<Position>)

class Node(val value: Int, val position: Position) {
    val others = mutableListOf<Node>()
    var isEnd = false
    var isStart = false
    override fun toString(): String {
        return "$value in x:${position.x} y:${position.y}"
    }
}

class Wall {
    var left = false
    var right = false
    var up = false
    var down = false

    fun info() = "left:$left right:$right up:$up down:$down"
}

class Graph private constructor(val width: Int, val height: Int) {
    val grid = Array(width) { Array(height) { Node(0, Position(-1, -1)) } }
    val walls = Array(width) { Array(height) { Wall() } }

    var start: Node? = null
    var end: Node? = null

    companion object Factory {
        private val rowRegex = Regex("""([+](-+|\s+))+[+]""") // +--+-+---+
        private val colRegex = Regex("""([+]([|]|\s))+[+]""") // +||+|+|||+

        fun createEmpty(width: Int, height: Int): Graph {
            require(width > 0 && height > 0)
            val graph = Graph(width, height)
            generateOuterWalls(graph)
            for (x in graph.grid.indices) {
                for (y in graph.grid[0].indices) {
                    graph.grid[x][y] = Node(0, Position(x, y))
                }
            }
            return graph
        }

        fun createFromLines(lines: List<String>): Graph {
            //checking lines lengths
            if (lines.any { it.length != lines.first().length })
                throw IllegalArgumentException("Different lines lengths")

            //checking rows
            if (lines.first().any { it !in "+-" } || lines.last().any { it !in "+-" })
                throw IllegalArgumentException("Bad upper/bottom border")
            for (row in lines.indices) {
                if (row % 2 == 0) {
                    if (!rowRegex.matches(lines[row]) || lines[row].replace(" ", "-") != lines.first())
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
                    if (!colRegex.matches(line))
                        throw IllegalArgumentException("Column #$col is wrong")
                }
            }

            val len = lines[0].length
            val graph = Graph(crosses.size - 1, lines.size / 2)
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
                        if (lines[row][col] == '|' && col < len - 1) {
                            graph.walls[x + 1][y].left = true
                            graph.walls[x][y].right = true
                        }
                        val value = buffer.replace(Regex("""\s"""), "")
                        when {
                            value.toIntOrNull() != null -> graph.grid[x][y] = Node(value.toInt(), Position(x, y))
                            value == "S" -> {
                                if (graph.start != null) throw IllegalArgumentException("Multiple start tiles")
                                val node = Node(0, Position(x, y)).apply { isStart = true }
                                graph.grid[x][y] = node
                                graph.start = node
                            }
                            value == "F" -> {
                                if (graph.end != null) throw IllegalArgumentException("Multiple finish tiles")
                                val node = Node(0, Position(x, y)).apply { isEnd = true }
                                graph.grid[x][y] = node
                                graph.end = node
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
                            if (lines[row - 1][col] == '-') {
                                graph.walls[x][y].up = true
                            }
                            if (lines[row + 1][col] == '-') {
                                graph.walls[x][y].down = true
                            }
                        }
                        buffer.append(lines[row][col])
                    }
                    col++
                }
                x = 0
            }
            if (graph.start == null) throw IllegalArgumentException("No start is present")
            if (graph.end == null) throw IllegalArgumentException("No end is present")
            generateOuterWalls(graph)
            return graph
        }

        private fun generateOuterWalls(graph: Graph) {
            for (i in 0 until graph.height) {
                graph.walls[0][i].left = true
                graph.walls[graph.width - 1][i].right = true
            }
            for (i in 0 until graph.width) {
                graph.walls[i][0].up = true
                graph.walls[i][graph.height - 1].down = true
            }
        }
    }

    fun solve(): Result {
        if (start == null || end == null) throw IllegalStateException("No end/start is present")
        link()

        val values = mutableMapOf<Node, Int>()
        values[start!!] = 0
        val parents = mutableMapOf<Node, Node?>()
        parents[start!!] = null

        val proceeded = mutableSetOf<Node>()
        val inProgress = ArrayDeque<Node>()
        val untouched = toList().toMutableSet()

        inProgress.add(start!!)
        untouched.remove(start!!)
        while(inProgress.isNotEmpty()) {
            val node = inProgress.pollFirst()
            proceeded.add(node)
            for (other in node.others) {
                if (other in untouched) {
                    values[other] = values[node]!! + other.value
                    parents[other] = node
                    inProgress.addLast(other)
                    untouched.remove(other)
                } else {
                    if ((generateSequence(node) { parents[it] }.all { it != other })
                            && (values[other]!! < values[node]!! + other.value)) {
                        if (other in proceeded) {
                            inProgress.addFirst(other)
                            proceeded.remove(other)
                        }
                        values[other] = values[node]!! + other.value
                        parents[other] = node
                    }
                }
            }
        }

        var node = end!!
        val path = mutableListOf(node.position)
        while (parents[node] != null) {
            node = parents[node]!!
            path.add(node.position)
        }

        return Result(values[end!!]!!, path.reversed())
    }

    //build dependencies in grid using walls
    private fun link() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val node = grid[x][y]
                node.others.clear()
                if (y > 0 && !walls[x][y].up) {
                    node.others.add(grid[x][y - 1])
                }
                if (y < height - 1 && !walls[x][y].down) {
                    node.others.add(grid[x][y + 1])
                }
                if (x > 0 && !walls[x][y].left) {
                    node.others.add(grid[x - 1][y])
                }
                if (x < width - 1 && !walls[x][y].right) {
                    node.others.add(grid[x + 1][y])
                }
            }
        }
    }

    private fun toList(): List<Node> {
        val list = mutableListOf<Node>()
        for (row in grid) {
            for (node in row) {
                list.add(node)
            }
        }
        return list
    }

    fun toLines(): List<String> {

        if (start == null || end == null) throw IllegalStateException("No start/end is present")
        val spaces = IntArray(width)
        for (y in 0 until height) {
            for (x in grid.indices) {
                val len = grid[x][y].value.toString().length
                spaces[x] = max(spaces[x], len)
            }
        }

        val border = "+" + spaces.joinToString("+") { "-".repeat(it) } + "+"

        val result = mutableListOf<String>()
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

                if (walls[x][y].right) line.append('|')
                else line.append(' ')

                if (walls[x][y].down) lowerLine.append("-".repeat(space) + "+")
                else lowerLine.append(" ".repeat(space) + "+")

                if (iterator.hasNext()) {
                    space = iterator.next()
                } else break
            }
            result.add(line.toString())
            result.add(lowerLine.toString())
        }
        result[result.lastIndex] = border
        return result
    }
}