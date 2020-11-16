package io.slava0135.cashfinder.model.graph
import io.slava0135.cashfinder.AppConfig
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

class Graph private constructor(val width: Int, val height: Int) {

    private val EMPTY = Node(0, Position(-1, -1))

    val grid = Array(width) { Array(height) { EMPTY } }
    val walls = Array(width) { Array(height) { Wall() } }

    var start: Node? = null
    var end: Node? = null

    companion object Factory {

        private val rowRegex = Regex("""([+](-+|\s+))+[+]""") // +--+-+---+
        private val colRegex = Regex("""([+]([|]|\s))+[+]""") // +|+|+|+

        fun createEmpty(width: Int, height: Int, random: Boolean = false): Graph {
            require(width > 0 && height > 0)
            val graph = Graph(width, height)
            graph.generateOuterWalls()
            if (random) {
                val generator = Random()
                for (x in graph.grid.indices) {
                    for (y in graph.grid[0].indices) {
                        graph.grid[x][y] = // Random value from -randomLimit to randomLimit
                                Node(generator.nextInt(AppConfig.Graph.randomLimit * 2 + 1) - AppConfig.Graph.randomLimit, Position(x, y))
                    }
                }
            } else {
                for (x in graph.grid.indices) {
                    for (y in graph.grid[0].indices) {
                        graph.grid[x][y] = Node(0, Position(x, y))
                    }
                }
            }
            return graph
        }

        fun load(file: File): Graph = createFromLines(file.readLines())

        fun createFromLines(lines: List<String>): Graph {

            require(lines.size > 2 && lines[0].length > 2)
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
                        val value = buffer.replace(Regex("\\s"), "")
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
            graph.validate()
            graph.generateOuterWalls()
            return graph
        }
    }

    private fun generateOuterWalls() {
        for (i in 0 until height) {
            walls[0][i].left = true
            walls[width - 1][i].right = true
        }
        for (i in 0 until width) {
            walls[i][0].up = true
            walls[i][height - 1].down = true
        }
    }

    fun generateAllWalls() {
        for (x in walls.indices) {
            for (y in walls[0].indices) {
                walls[x][y].apply {
                    up = true
                    down = true
                    left = true
                    right = true
                }
            }
        }
    }

    fun generateRandomWalls() {

        generateAllWalls()

        val random = Random()

        val (initX, initY) = Pair(random.nextInt(width), random.nextInt(height))
        val queue = ArrayDeque<Position>()  // Path
        queue.add(Position(initX, initY))

        val isVisited = Array(width) { BooleanArray(height) }
        isVisited[initX][initY] = true
        // Depth-first maze generation
        while (queue.isNotEmpty()) {

            val current = queue.first
            val (x, y) = current

            fun tryPath(unblock: Boolean): Boolean {
                if (x > 0 && !isVisited[x - 1][y] && (unblock || random.nextBoolean())) {
                    walls[x - 1][y].right = false
                    walls[x][y].left = false
                    isVisited[x - 1][y] = true
                    queue.addFirst(Position(x - 1, y))
                }
                else if (y > 0 && !isVisited[x][y - 1] && (unblock || random.nextBoolean())) {
                    walls[x][y - 1].down = false
                    walls[x][y].up = false
                    isVisited[x][y - 1] = true
                    queue.addFirst(Position(x, y - 1))
                }
                else if (x < width - 1 && !isVisited[x + 1][y] && (unblock || random.nextBoolean())) {
                    walls[x + 1][y].left = false
                    walls[x][y].right = false
                    isVisited[x + 1][y] = true
                    queue.addFirst(Position(x + 1, y))
                }
                else if (y < height - 1 && !isVisited[x][y + 1] && (unblock || random.nextBoolean())) {
                    walls[x][y + 1].up = false
                    walls[x][y].down = false
                    isVisited[x][y + 1] = true
                    queue.addFirst(Position(x, y + 1))
                } else return true
                return false
            }

            if (tryPath(false)) {
                if (tryPath(true)) {
                    queue.removeFirst()
                }
            }
        }

        for (x in 0 until width) {
            for (y in (x % 2) until height step(2)) {
                if (x > 0 && random.nextInt(100) < 20) {  // 20% chance that the wall will be removed
                    walls[x - 1][y].right = false
                    walls[x][y].left = false
                }
                if (y > 0 && random.nextInt(100) < 20) {
                    walls[x][y - 1].down = false
                    walls[x][y].up = false
                }
                if (x < width - 1 && random.nextInt(100) < 20) {
                    walls[x + 1][y].left = false
                    walls[x][y].right = false
                }
                if (y < height - 1 && random.nextInt(100) < 20) {
                    walls[x][y + 1].up = false
                    walls[x][y].down = false
                }
            }
        }

        generateOuterWalls()
    }

    fun save(file: File) {
        validate()
        file.writeText(toString())
    }

    //build dependencies in grid using walls
    fun link() {
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

    fun nodeList(): List<Node> {
        val list = mutableListOf<Node>()
        for (row in grid) {
            for (node in row) {
                list.add(node)
            }
        }
        return list
    }

    fun toLines(): List<String> {
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

    fun changeSize(up: Int = 0, right: Int = 0, down: Int = 0, left: Int = 0, allWalls: Boolean = false, random: Boolean = false): Graph {

        if (up + down + height < 1 || left + right + width < 1) throw IllegalStateException("Negative size values")
        if (up + down + height > AppConfig.Graph.sizeLimit || left + right + width > AppConfig.Graph.sizeLimit) throw IllegalStateException("Size limit reached")

        val newGraph = createEmpty(left + right + width, up + down + height, random)
        if (allWalls) newGraph.generateAllWalls()

        for (col in max(0, -left) until min(width, width + right)) {
            for (row in max(0, -up) until min(height, height + down)) {
                val newX = col + left
                val newY = row + up
                newGraph.grid[newX][newY] = Node(grid[col][row].value, Position(newX, newY)).apply {
                    isStart = grid[col][row].isStart
                    isEnd = grid[col][row].isEnd
                }
                if (walls[col][row].up && row == 0 && newY > 0) {
                    newGraph.walls[newX][newY - 1].down = true
                }
                if (walls[col][row].down && row == height - 1 && newY + 1 < newGraph.height) {
                    newGraph.walls[newX][newY + 1].up = true
                }
                if (walls[col][row].left && col == 0 && newX > 0) {
                    newGraph.walls[newX - 1][newY].right = true
                }
                if (walls[col][row].right && col == width - 1 && newX + 1 < newGraph.width) {
                    newGraph.walls[newX + 1][newY].left = true
                }
                newGraph.walls[newX][newY].apply {
                    if (!this.up) this.up = walls[col][row].up
                    if (!this.down) this.down = walls[col][row].down
                    if (!this.right) this.right = walls[col][row].right
                    if (!this.left) this.left = walls[col][row].left
                }
            }
        }

        return newGraph
    }

    override fun toString(): String = toLines().joinToString("\n")

    fun validate() {
        if (start == null) throw IllegalStateException("Start is not found")
        if (end == null) throw IllegalStateException("Finish is not found")
    }
}