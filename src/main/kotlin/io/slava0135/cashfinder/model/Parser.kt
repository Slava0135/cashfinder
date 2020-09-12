package io.slava0135.cashfinder.model

fun parse(lines: List<String>): Graph {
    validate(lines)

    val crosses = mutableSetOf<Int>()
    for (index in lines[0].indices) {
        if (lines[0][index] == '+') {
            crosses.add(index)
        }
    }

    val len = lines[0].length
    val graph = Graph(crosses.size - 1, lines.size / 2)
    var x = 0
    var y = 0
    //only rows with digits
    for (row in 1 until lines.size step 2) {
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
                if (value.toIntOrNull() != null) graph.grid[x][y] = Node(value.toInt(), Position(x, y))
                else if (value == "S") {
                    if (graph.start != null) throw IllegalArgumentException("Multiple start tiles")
                    val node = Node(0, Position(x, y)).apply { isStart = true }
                    graph.grid[x][y] = node
                    graph.start = node
                }
                else if (value == "F") {
                    if (graph.end != null) throw IllegalArgumentException("Multiple finish tiles")
                    val node = Node(0, Position(x, y)).apply { isEnd = true }
                    graph.grid[x][y] = node
                    graph.end = node
                } else throw IllegalArgumentException("Invalid tile x:$x y:$y")
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
        y++
    }
    if (graph.start == null) throw IllegalArgumentException("No start present")
    if (graph.end == null) throw IllegalArgumentException("No end present")
    for (i in 0 until graph.height) {
        graph.walls[0][i].left = true
        graph.walls[graph.width - 1][i].right = true
    }
    for (i in 0 until graph.width) {
        graph.walls[i][0].up = true
        graph.walls[i][graph.height - 1].down = true
    }
    return graph
}

private val rowRegex = Regex("""([+](-+|\s+))+[+]""") // +--+-+---+
private val colRegex = Regex("""([+]([|]|\s))+[+]""") // +||+|+|||+

fun validate(lines: List<String>) {
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
}