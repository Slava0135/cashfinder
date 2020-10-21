package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import tornadofx.*

class SolutionFrame(private val graph: SolvedGraph) : Fragment("Solution") {
    override val root = borderpane {
        setPrefSize(AppConfig.Window.prefHeight.toDouble(), AppConfig.Window.prefWidth.toDouble())
        left = SolutionTotal(graph).root
        center = SolutionView(graph).root
    }
}