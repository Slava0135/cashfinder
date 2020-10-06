package io.slava0135.cashfinder.model

import java.util.ArrayDeque

class Solution(val nodes: List<Node>, val initial: Int, val score: Int?)

enum class Solver(val type: String, private val function: (Graph, Int) -> SolvedGraph) {
    BRUTEFORCE("Precise", ::bruteforce);

    fun solve(graph: Graph, initial: Int) = function(graph, initial)
    override fun toString() = type
}

private fun bruteforce(graph: Graph, initial: Int): SolvedGraph {
    var best: Pair<List<Node>, Int>? = null
    val path = ArrayDeque<Node>()
    graph.link()
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
                    path.addLast(other)
                    next(other, newValue)
                    path.removeLast()
                }
            }
        }
    }
    next(graph.start!!, initial)
    return if (best == null) {
        SolvedGraph(graph, Solution(emptyList(), initial, null))
    } else SolvedGraph(graph, Solution(best!!.first.toList(), initial, best!!.second))
}