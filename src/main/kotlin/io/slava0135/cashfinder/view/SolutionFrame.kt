package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.SolvedGraph
import tornadofx.*

class SolutionFrame(val graph: SolvedGraph) : Fragment("Solution") {
    override val root = hbox()
}