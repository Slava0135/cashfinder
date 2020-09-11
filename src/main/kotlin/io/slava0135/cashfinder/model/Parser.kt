package io.slava0135.cashfinder.model

fun parse(lines: List<String>): Graph {
    validate(lines)

    //reading crosses positions
    val crosses = mutableSetOf<Int>()
    for (index in lines[0].indices) {
        if (lines[0][index] == '+') {
            crosses.add(index)
        }
    }

    val graph = Graph(crosses.size - 1, lines.size / 2)
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
    println(validate(lines))
}