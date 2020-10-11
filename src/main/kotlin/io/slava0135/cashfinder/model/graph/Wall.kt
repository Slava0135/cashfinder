package io.slava0135.cashfinder.model.graph

class Wall {
    var left = false
    var right = false
    var up = false
    var down = false

    override fun toString() = "left:$left right:$right up:$up down:$down"
}