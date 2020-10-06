package io.slava0135.cashfinder.model

enum class Solver(val type: String, val solve: (Graph, Int) -> SolvedGraph) {
    BRUTEFORCE("Precise", ::bruteforce)
}

private fun bruteforce(graph: Graph, initial: Int): SolvedGraph {
    TODO()
}