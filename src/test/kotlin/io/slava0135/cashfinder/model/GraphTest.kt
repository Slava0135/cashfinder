package io.slava0135.cashfinder.model

import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.*
import java.io.File

internal class GraphTest {

    fun getGraph(name: String) = Graph.createFromLines(getText(name))
    fun getSolvedGraph(name: String) = SolvedGraph.createFromLines(getText(name))
    fun getText(name: String) = {}.javaClass.getResource(name).readText().split(Regex("""\n"""))

    fun File.parse() = this.readText().split(Regex("""\n"""))

    @Test
    fun solve() {
        assertEquals(32, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/simple.csh"), 0).score)
        assertEquals(34, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/all.csh"), 0).score)
        assertEquals(7, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/money.csh"), 0).score)
        assertEquals(12, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/money.csh"), 1).score)
    }

    @Test
    fun testIO() {

        val t = "temp.txt"

        try {
            File(t).createNewFile()

            var graph = getGraph("/io/IOtest1.csh")
            graph.save(File(t))
            assertEquals(File(t).parse(), getText("/io/IOtest1.csh"))

            graph = getGraph("/io/IOtest2.csh")
            graph.save(File(t))
            assertEquals(File(t).parse(), getText("/io/IOtest2.csh"))

            graph = getGraph("/io/IOtest3.csh")
            graph.save(File(t))
            assertEquals(File(t).parse(), getText("/io/IOtest3.csh"))

            val solvedGraph = getSolvedGraph("/io/IOtest4.sol")
            solvedGraph.save(File(t))
            assertEquals(File(t).parse(), getText("/io/IOtest4.sol"))

        } finally {
            File(t).delete()
        }
    }

    @Test
    fun validateGraph() {
        assertFailsWith<Exception> {
            getGraph("/validation/empty.csh")
        }
        assertFailsWith<Exception> {
            getGraph("/validation/invalidvalue.csh")
        }
        assertFailsWith<Exception> {
            getGraph("/validation/missingouterwalls.csh")
        }
        assertFailsWith<Exception> {
            getGraph("/validation/nofinish.csh")
        }
        assertFailsWith<Exception> {
            getGraph("/validation/nostart.csh")
        }
        assertFailsWith<Exception> {
            getGraph("/validation/offset.csh")
        }
    }

    @Test
    fun validateSolvedGraph() {
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/emptyS.sol")
        }
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/invalidvalueS.sol")
        }
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/missingouterwallsS.sol")
        }
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/nofinishS.sol")
        }
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/nostartS.sol")
        }
        assertFailsWith<Exception> {
            getSolvedGraph("/validation/offsetS.sol")
        }
    }

    @Test
    fun changeSize() {

    }
}