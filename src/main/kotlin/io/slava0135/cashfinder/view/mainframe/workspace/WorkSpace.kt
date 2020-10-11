package io.slava0135.cashfinder.view.mainframe.workspace

import io.slava0135.cashfinder.view.mainframe.graph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*

class Workspace: Fragment() {

    init {
        graph.addListener {
            _, _, _ -> replaceWith<Workspace>()
        }
    }

    override val root = scrollpane {
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        setPrefSize(800.0, 450.0)
        content = gridpane {
            isPannable = true
            if (graph.value != null) {
                for (y in 0..graph.value.grid[0].size * 2) {
                    row {
                        for (x in 0..graph.value.grid.size * 2) {
                            when {
                                y % 2 == 0 && x % 2 == 0 -> {
                                    rectangle(width = 8, height = 8) { fill = Color.BLACK }
                                }
                                y % 2 == 0 -> {
                                    vWall(this, x, y)
                                }
                                x % 2 == 0 -> {
                                    hWall(this, x, y)
                                }
                                else -> {
                                    tile(this, x, y)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun vWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = 64, height = 8).apply {
                fill = if (y == 0 || y == graph.value.grid[0].size * 2 || graph.value.walls[x / 2][y / 2].up) Color.BLACK else Color.WHITE
                setOnMouseClicked {
                    if (y != 0 && y != graph.value.grid[0].size * 2) {
                        if (fill == Color.BLACK) {
                            graph.value.walls[x / 2][y / 2].up = false
                            graph.value.walls[x / 2][y / 2 - 1].down = false
                            fill = Color.WHITE
                        } else {
                            graph.value.walls[x / 2][y / 2].up = true
                            graph.value.walls[x / 2][y / 2 - 1].down = true
                            fill = Color.BLACK
                        }
                    }
                }
            }
        }
    }

    private fun hWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = 8, height = 64).apply {
                fill = if (x == 0 || x == graph.value.grid.size * 2 || graph.value.walls[x / 2][y / 2].left) Color.BLACK else Color.WHITE
                setOnMouseClicked {
                    if (x != 0 && x != graph.value.grid.size * 2) {
                        if (fill == Color.BLACK) {
                            graph.value.walls[x / 2 - 1][y / 2].right = false
                            graph.value.walls[x / 2][y / 2].left = false
                            fill = Color.WHITE
                        } else {
                            graph.value.walls[x / 2 - 1][y / 2].right = true
                            graph.value.walls[x / 2][y / 2].left = true
                            fill = Color.BLACK
                        }
                    }
                }
            }
        }
    }

    private fun tile(pane: Pane, x: Int, y: Int) {
        pane.apply {
            textfield {
                prefWidth = 64.0
                prefHeight = 64.0
                alignment = Pos.CENTER
                font = Font.font(20.0)
                val elem = graph.value.grid[x / 2][y / 2]
                text = when {
                    elem.isStart -> {
                        background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                        "S"
                    }
                    elem.isEnd -> {
                        background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                        "F"
                    }
                    else -> {
                        background = Background.EMPTY
                        elem.value.toString()
                    }
                }
                filterInput {
                    it.controlNewText.let {
                        newText ->
                        (newText == "S" && graph.value.start == null)
                                || (newText == "F" && graph.value.end == null)
                                || newText == "-" || (newText.isInt() && newText.toInt() in -99..99)
                    }
                }
                setOnKeyReleased {
                    when (text) {
                        "S" -> {
                            val node = graph.value.grid[x / 2][y / 2]
                            node.apply {
                                value = 0
                                isStart = true
                                isEnd = false
                            }
                            graph.value.start = node
                            background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                        }
                        "F" -> {
                            val node = graph.value.grid[x / 2][y / 2]
                            node.apply {
                                value = 0
                                isStart = false
                                isEnd = true
                            }
                            graph.value.end = node
                            background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                        }
                        else -> {
                            val node = graph.value.grid[x / 2][y / 2]
                            if (node.isStart) {
                                node.isStart = false
                                graph.value.start = null
                                background = Background.EMPTY
                            }
                            if (node.isEnd) {
                                node.isEnd = false
                                graph.value.end = null
                                background = Background.EMPTY
                            }
                            node.apply {
                                value = if (text == "-" || text == "") 0 else text.toInt()
                                isStart = false
                                isEnd = false
                            }
                        }
                    }
                }
            }
        }
    }
}