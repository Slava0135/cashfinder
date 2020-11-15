package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import tornadofx.*

// Window for viewing Solution created from GraphSolutionMenu
class SolutionFrame(private val graph: SolvedGraph) : Fragment("Solution") {
    override val root = borderpane {
        left = SolutionTotal(graph).root
        center = SolutionView(graph).root
    }
}