package io.slava0135.cashfinder.model

fun parse(lines: List<String>): List<Node> {
    TODO()
}

private val rowRegex = Regex("""([+](-+|\s+))+[+]""")
private val colRegex = Regex("""([+]([|]|\s))+[+]""")

fun validate(lines: List<String>): String {
    //unified length
    if (lines.any { it.length != lines.first().length }) return "Different line lengths"

    //checking rows
    if (lines.first().any { it !in "+-" } || lines.last().any { it !in "+-" }) return "Bad upper/bottom border"
    for (row in lines.indices) {
        if (row % 2 == 0) {
            if (!rowRegex.matches(lines[row]) || lines[row].replace(" ", "-") != lines.first()) return "Row #$row is wrong"
        }
    }

    //reading crosses positions
    val crosses = mutableSetOf<Int>()
    for (index in lines[0].indices) {
        if (lines[0][index] == '+') {
            crosses.add(index)
        }
    }

    //minimum requirements
    if (crosses.size < 4 || lines.size < 3) return "Maze is too small"

    //checking columns
    fun rotate(index: Int) = lines.map { it[index] }.joinToString("")
    val firstCol = rotate(0)
    if (firstCol.any { it !in "+|" } || rotate(lines[0].length - 1).any { it !in "+|" }) return "Bad left/right border"
    for (col in lines[0].indices) {
        val line = rotate(col)
        if (col in crosses) {
            if (!colRegex.matches(line)) return "Column #$col is wrong"
        }
    }
    return ""
}

fun main() {
    val lines = {}.javaClass.getResource("/test1").readText().split("\n")
    println(validate(lines))
}