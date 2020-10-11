package io.slava0135.cashfinder.model.solvedgraph

class SolvedWall {
    var up = WallState.NO_WALL
    var right = WallState.NO_WALL
    var left = WallState.NO_WALL
    var down = WallState.NO_WALL

    enum class WallState {
        NO_WALL,
        WALL,
        ON_PATH
    }
}