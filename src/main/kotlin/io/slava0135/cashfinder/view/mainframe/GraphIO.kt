package io.slava0135.cashfinder.view.mainframe

import io.slava0135.cashfinder.model.graph.Graph
import tornadofx.*
import java.io.File

internal val graph = objectProperty<Graph>()

internal fun save(file: File) {
    graph.value.validate()
    file.writeText(graph.value.toString())
}

internal fun load(file: File) {
    graph.value = Graph.createFromLines(file.readLines())
}