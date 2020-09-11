package io.slava0135.cashfinder.model

import java.lang.Integer.max
import java.lang.Integer.min

fun parse(lines: List<String>): Graph {
    validate(lines)
    val len = lines[0].length

    //reading crosses positions
    val crosses = mutableSetOf<Int>()
    for (index in lines[0].indices) {
        if (lines[0][index] == '+') {
            crosses.add(index)
        }
    }

    val graph = Graph(crosses.size - 1, lines.size / 2)
    var x = 0
    var y = 0
    for (row in 1 until lines.size step 2) {
        val iterator = crosses.iterator()
        var cross = iterator.next()
        var col = 0
        val buffer = StringBuilder()
        while (col < len) {
            if (col == cross) {
                if (buffer.isNotEmpty()) {
                    val value = buffer.replace(Regex("""\s"""), "")
                    if (value.toIntOrNull() != null) graph.grid[x][y] = Node(value.toInt(), Position(x, y))
                    else if (value == "S") {
                        if (graph.start != null) throw IllegalArgumentException("Multiple start tiles")
                        val node = Node(0, Position(x, y))
                        graph.grid[x][y] = node
                        graph.start = node
                    }
                    else if (value == "F") {
                        if (graph.end != null) throw IllegalArgumentException("Multiple finish tiles")
                        val node = Node(0, Position(x, y))
                        graph.grid[x][y] = node
                        graph.end = node
                    } else throw IllegalArgumentException("Invalid tile x:$x y:$y")
                    buffer.clear()
                    x++
                } else if (lines[row][col] == '|') {
                    graph.walls[max(x, 0)][y].right = true
                    graph.walls[min(x, len - 1)][y].left = true
                }
                if (!iterator.hasNext()) break
                cross = iterator.next()
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
    graph.link()
    return graph
}

private val rowRegex = Regex("""([+](-+|\s+))+[+]""")
private val colRegex = Regex("""([+]([|]|\s))+[+]""")

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

fun main() {
    val lines = {}.javaClass.getResource("/test1").readText().split("\n")
    println(parse(lines))
}