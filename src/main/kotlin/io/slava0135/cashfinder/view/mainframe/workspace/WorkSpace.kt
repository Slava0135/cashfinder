package io.slava0135.cashfinder.view.mainframe.workspace

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.view.mainframe.graph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*

class Workspace: Fragment() {

    override val root = scrollpane {

        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        setPrefSize(AppConfig.Window.prefWidth.toDouble(), AppConfig.Window.prefHeight.toDouble())

        content = gridpane {
            isPannable = true
            if (graph.value != null) {
                for (y in 0..graph.value.grid[0].size * 2) {
                    newRow(this, y)
                }
            }
        }
    }

    private fun newRow(pane: GridPane, y: Int) {
        pane.apply {
            row {
                for (x in 0..graph.value.grid.size * 2) {
                    when {
                        y % 2 == 0 && x % 2 == 0 -> {
                            rectangle(width = AppConfig.GridPane.baseWidth, height = AppConfig.GridPane.baseWidth) { fill = Color.BLACK }
                        }
                        y % 2 == 0 -> {
                            newHorizontalWall(this, x, y)
                        }
                        x % 2 == 0 -> {
                            newVerticalWall(this, x, y)
                        }
                        else -> {
                            newTile(this, x, y)
                        }
                    }
                }
            }
        }
    }

    private fun newHorizontalWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = AppConfig.GridPane.baseLength, height = AppConfig.GridPane.baseWidth).apply {

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

    private fun newVerticalWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = AppConfig.GridPane.baseWidth, height = AppConfig.GridPane.baseLength).apply {

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

    private fun newTile(pane: Pane, x: Int, y: Int) {
        pane.apply {
            textfield {

                prefWidth = AppConfig.GridPane.baseLength.toDouble()
                prefHeight = AppConfig.GridPane.baseLength.toDouble()
                alignment = Pos.CENTER
                font = AppConfig.font

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
                                || newText == "-"
                                || (newText.isInt() && newText.toInt() in -AppConfig.Graph.valueLimit..AppConfig.Graph.valueLimit)
                    }
                }

                setOnKeyReleased {
                    when (text) {
                        "S" -> {
                            val node = graph.value.grid[x / 2][y / 2]
                            if (node.isEnd) {
                                node.isEnd = false
                                graph.value.end = null
                            }
                            node.apply {
                                value = 0
                                isStart = true
                            }
                            graph.value.start = node
                            background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                        }
                        "F" -> {
                            val node = graph.value.grid[x / 2][y / 2]
                            if (node.isStart) {
                                node.isStart = false
                                graph.value.start = null
                            }
                            node.apply {
                                value = 0
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