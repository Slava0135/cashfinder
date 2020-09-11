package io.slava0135.cashfinder.model

import java.lang.IllegalArgumentException
import java.lang.Integer.max

class Position(val x: Int, val y: Int)

class Node(val value: Int, val position: Position) {
    val nodes = mutableListOf<Node>()
    var isEnd = false
    var isStart = false
    override fun toString(): String {
        return "$value in x:${position.x} y:${position.y}"
    }
}

class Wall {
    var left = false
    var right = false
    var up = false
    var down = false

    override fun toString() = "left:$left right:$right up:$up down:$down"
}

class Graph(val width: Int, val height: Int) {
    val grid = Array(width) { Array(height) { Node(0, Position(-1, -1)) } }
    val walls = Array(width) { Array(height) { Wall() } }

    var start: Node? = null
    var end: Node? = null

    //build dependencies in grid using walls
    fun link() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (y > 0 && !walls[x][y].up) {
                    grid[x][y].nodes.add(grid[x][y - 1])
                }
                if (y < height - 1 && !walls[x][y].down) {
                    grid[x][y].nodes.add(grid[x][y + 1])
                }
                if (x > 0 && !walls[x][y].left) {
                    grid[x][y].nodes.add(grid[x - 1][y])
                }
                if (x < width - 1 && !walls[x][y].right) {
                    grid[x][y].nodes.add(grid[x + 1][y])
                }
            }
        }
    }

    //return list of linked nodes
    fun toList(): List<Node> {
        val list = mutableListOf<Node>()
        for (row in grid) {
            for (node in row) {
                list.add(node)
            }
        }
        return list
    }

    fun toLines(): List<String> {
        val spaces = IntArray(width)
        for (y in 0 until height) {
            for (x in grid.indices) {
                val len = grid[x][y].value.toString().length
                spaces[x] = max(spaces[x], len)
            }
        }
        val border = "+" + spaces.map { "-".repeat(it) }.joinToString("+") + "+"
        val result = mutableListOf<String>()
        result.add(border)
        for (y in 0 until height) {
            val line = StringBuilder("|")
            val lowerLine = StringBuilder("+")
            val iterator = spaces.iterator()
            var space = iterator.next()
            for (x in 0 until width) {
                if (grid[x][y].isStart) {
                    line.append("S".padStart(space))
                } else if (grid[x][y].isEnd) {
                    line.append("F".padStart(space))
                } else {
                    line.append(grid[x][y].value.toString().padStart(space))
                }

                if (walls[x][y].right) {
                    line.append('|')
                } else {
                    line.append(' ')
                }

                if (walls[x][y].down) {
                    lowerLine.append("-".repeat(space) + "+")
                } else {
                    lowerLine.append(" ".repeat(space) + "+")
                }

                if (iterator.hasNext()) {
                    space = iterator.next()
                } else break
            }
            result.add(line.toString())
            result.add(lowerLine.toString())
        }
        result[result.lastIndex] = border
        return result
    }
}