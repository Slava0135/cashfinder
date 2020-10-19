package io.slava0135.cashfinder.view.mainframe

import io.slava0135.cashfinder.model.graph.Graph
import tornadofx.*
import java.io.File

internal val graph = objectProperty<Graph>()

internal fun loadGraph(file: File) {
    graph.value = Graph.load(file)
}