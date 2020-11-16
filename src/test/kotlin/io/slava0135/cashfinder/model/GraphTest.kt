package io.slava0135.cashfinder.model

import io.slava0135.cashfinder.model.graph.Graph.Factory.createFromLines
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.*
import java.io.File

internal class GraphTest {

    fun getGraph(name: String) = createFromLines(getText(name))
    fun getText(name: String) = {}.javaClass.getResource(name).readText().split(Regex("""\n"""))
    fun File.parse() = this.readText().split(Regex("""\n"""))

    @Test
    fun solve() {
        //var lines = {}.javaClass.getResource("/pathfinding/test1.csh").readText().split(Regex("""\n"""))
        //println(Graph.createFromLines(lines).solve().path)
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
        } finally {
            File(t).delete()
        }
    }

    @Test
    fun validate() {
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
}