package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import java.io.File

internal fun SolvedGraph.save(file: File) {
    file.writeText(toString())
}

internal fun SolvedGraph.load(file: File) = SolvedGraph.createFromLines(file.readLines())