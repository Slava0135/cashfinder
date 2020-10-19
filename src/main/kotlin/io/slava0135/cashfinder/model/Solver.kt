package io.slava0135.cashfinder.model

import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.graph.Node
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph

class Solution(val nodes: List<Node>, val initial: Int, val score: Int?)

enum class Solver(val type: String, private val function: (Graph, Int) -> SolvedGraph) {
    BRUTEFORCE("Precise", ::bruteforce);

    fun solve(graph: Graph, initial: Int): SolvedGraph {
        graph.link()
        return function(graph, initial)
    }

    override fun toString() = type
}

private fun bruteforce(graph: Graph, initial: Int): SolvedGraph {
    var best: Pair<List<Node>, Int>? = null
    val path = LinkedHashSet<Node>()
    path.add(graph.start!!)
    fun next(node: Node, value: Int) {
        if (node.isEnd) {
            if (best == null || best!!.second < value || (best!!.second == value && best!!.first.size > path.size)) {
                best = Pair(path.toList(), value)
            }
        } else {
            for (other in node.others) {
                val newValue = value + other.value
                if (other !in path && newValue >= 0) {
                    path.add(other)
                    next(other, newValue)
                    path.remove(other)
                }
            }
        }
    }
    next(graph.start!!, initial)
    return if (best == null) {
        SolvedGraph.createFromSolution(graph, Solution(emptyList(), initial, null))
    } else SolvedGraph.createFromSolution(graph, Solution(best!!.first.toList(), initial, best!!.second))
}