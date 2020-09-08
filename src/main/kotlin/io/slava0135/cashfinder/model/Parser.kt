package io.slava0135.cashfinder.model

fun parse(lines: List<String>): List<Node> {
    TODO()
}

private fun validate(lines: List<String>): Boolean {
    require(lines.size > 2 && lines[0].length > 4)
    val crosses = mutableSetOf<Int>()

    for (index in lines[0].indices) {

        if (lines[index] == "+") crosses.add(index)
    }

    for (i in 2..lines.size step 2) {
        for (j in lines[i].indices) {
            if (j in crosses) {

            }
        }
    }
}