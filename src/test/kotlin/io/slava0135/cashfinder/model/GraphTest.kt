package io.slava0135.cashfinder.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GraphTest {

    @Test
    fun solve() {
        var lines = {}.javaClass.getResource("/pathfinding/test1").readText().split(Regex("""\n"""))
        println(parse(lines).solve().path)
        lines = {}.javaClass.getResource("/pathfinding/test2").readText().split(Regex("""\n"""))
        println(parse(lines).solve().path)
        lines = {}.javaClass.getResource("/pathfinding/test3").readText().split(Regex("""\n"""))
        println(parse(lines).solve().path)
    }

    @Test
    fun toLines() {

    }
}