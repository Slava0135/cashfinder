package io.slava0135.cashfinder.model
import java.lang.Integer.max
import java.util.*

data class Position(val x: Int, val y: Int)

class Result(val cash: Int, val path: List<Position>)

class Node(val value: Int, val position: Position) {
    val others = mutableListOf<Node>()
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

    fun info() = "left:$left right:$right up:$up down:$down"
}

class Graph(val width: Int, val height: Int) {
    val grid = Array(width) { Array(height) { Node(0, Position(-1, -1)) } }
    val walls = Array(width) { Array(height) { Wall() } }

    var start: Node? = null
    var end: Node? = null

    fun solve(): Result {
        if (start == null || end == null) throw IllegalStateException("No end/start")
        link()

        val values = mutableMapOf<Node, Int>()
        values[start!!] = 0
        val parents = mutableMapOf<Node, Node?>()
        parents[start!!] = null

        val proceeded = mutableSetOf<Node>()
        val inProgress = ArrayDeque<Node>()
        inProgress.add(start!!)
        val untouched = toList().toMutableSet()
        untouched.remove(start!!)

        while(inProgress.isNotEmpty()) {
            val node = inProgress.pollFirst()
            proceeded.add(node)
            for (other in node.others) {
                if (other in untouched) {
                    values[other] = values[node]!! + other.value
                    parents[other] = node
                    inProgress.addLast(other)
                    untouched.remove(other)
                } else {
                    if (generateSequence(node) { parents[it] }.all { it != other} && values[other]!! < values[node]!! + other.value) {
                        if (other in proceeded) {
                            inProgress.addFirst(other)
                            proceeded.remove(other)
                        }
                        values[other] = values[node]!! + other.value
                        parents[other] = node
                    }
                }
            }
        }

        var node = end!!
        val path = mutableListOf(node.position)
        while (parents[node] != null) {
            node = parents[node]!!
            path.add(node.position)
        }

        return Result(values[end!!]!!, path.reversed())
    }

    //build dependencies in grid using walls
    private fun link() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val node = grid[x][y]
                node.others.clear()
                if (y > 0 && !walls[x][y].up) {
                    node.others.add(grid[x][y - 1])
                }
                if (y < height - 1 && !walls[x][y].down) {
                    node.others.add(grid[x][y + 1])
                }
                if (x > 0 && !walls[x][y].left) {
                    node.others.add(grid[x - 1][y])
                }
                if (x < width - 1 && !walls[x][y].right) {
                    node.others.add(grid[x + 1][y])
                }
            }
        }
    }

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