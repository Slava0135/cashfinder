package io.slava0135.cashfinder.model

import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.*
import java.io.File

internal class ModelTest {

    fun getGraph(name: String) = Graph.createFromLines(getText(name))
    fun getSolvedGraph(name: String) = SolvedGraph.createFromLines(getText(name))
    fun getText(name: String) = {}.javaClass.getResource(name).readText().split(Regex("""\n"""))

    fun File.parse() = this.readText().split(Regex("""\n"""))

    @Test
    fun solve() {
        //simple test, no initial money, 32 is max possible score
        assertEquals(32, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/simple.csh"), 0).score)
        //algorithm has to traverse through all rooms, no initial money, 34 is max possible score
        assertEquals(34, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/all.csh"), 0).score)
        //amount of initial money matters
        //no initial money, 7 is max possible score
        assertEquals(7, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/money.csh"), 0).score)
        //now with initial money - 12 is max possible score instead of 7
        assertEquals(12, Solver.BRUTEFORCE.solve(getGraph("/pathfinding/money.csh"), 1).score)
    }

    @Test
    fun testIO() {

        val t = "temp.txt"
        //testing if input file's content equals output file's content for both solved and data graphs
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
        //invalid file format
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
        //invalid file format
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
        val graph = getGraph("/changesize/input.csh")
        //add 3 columns on the right and 3 columns on the left
        assertEquals(graph.changeSize(right = 3, left = 3).toString(), getGraph("/changesize/output1.csh").toString())
        //remove 2 rows from the top and add 1 column on the right
        assertEquals(graph.changeSize(up = - 2, right = 1).toString(), getGraph("/changesize/output2.csh").toString())
    }
}