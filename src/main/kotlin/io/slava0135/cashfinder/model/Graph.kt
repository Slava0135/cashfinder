package io.slava0135.cashfinder.model

class Position(val x: Int, val y: Int)

class Node(val value: Int, val position: Position) {
    val nodes = mutableListOf<Node>()
    override fun toString(): String {
        return "$value in x:${position.x} y:${position.y}"
    }
}

class Wall {
    var left = false
    var right = false
    var up = false
    var down = false
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

    override fun toString(): String {
        TODO()
    }
}