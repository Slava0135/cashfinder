package io.slava0135.cashfinder.view.mainframe

import io.slava0135.cashfinder.model.graph.Graph
import tornadofx.*
import java.io.File

internal val graph = objectProperty<Graph>()

internal fun Graph.save(file: File) {
    validate()
    file.writeText(toString())
}

internal fun Graph.Factory.load(file: File) {
    graph.value = createFromLines(file.readLines())
}