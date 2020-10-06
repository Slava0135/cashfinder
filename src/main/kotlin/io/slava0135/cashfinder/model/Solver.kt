package io.slava0135.cashfinder.model

enum class Solver(val type: String, private val function: (Graph, Int) -> SolvedGraph) {
    BRUTEFORCE("Precise", ::bruteforce);

    fun solve(graph: Graph, initial: Int) = function(graph, initial)
    override fun toString() = type
}

private fun bruteforce(graph: Graph, initial: Int): SolvedGraph {
    TODO()
}