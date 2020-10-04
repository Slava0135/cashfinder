package io.slava0135.cashfinder.model

import java.io.File
import java.lang.IllegalStateException

object Model {
    var graph: Graph? = null

    fun save(file: File) {
        if (graph == null) throw IllegalStateException("No graph is present")
        file.writeText(graph.toString())
    }

    fun load(file: File) {
        graph = Graph.createFromLines(file.readLines())
    }
}