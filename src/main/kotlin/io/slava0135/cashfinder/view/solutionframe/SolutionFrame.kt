package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.Solution
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import io.slava0135.cashfinder.model.solvedgraph.SolvedWall
import io.slava0135.cashfinder.view.mainframe.graph
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class SolutionFrame(private val graph: SolvedGraph) : Fragment("Solution") {
    override val root = borderpane {
        left = SolutionTotal(graph.solution).root
        right = SolutionView(graph).root
    }
}

class SolutionTotal(solution: Solution) : Fragment() {
    override val root = vbox {
        pane {
            form {
                fieldset{
                    field {
                        label("Initial money: ${solution.initial}") {
                            font = AppConfig.font
                        }
                    }
                    if (solution.score != null) {
                        field {
                            label("Total money: ${solution.score}") {
                                font = AppConfig.font
                            }
                        }
                        field {
                            label("Path length: ${solution.nodes.size}") {
                                font = AppConfig.font
                            }
                        }
                    } else {
                        field {
                            label("Solution is not found") {
                                font = AppConfig.font
                            }
                        }
                    }
                }
            }
        }
    }
}

class SolutionView(private val graph: SolvedGraph) : Fragment() {
    override val root = scrollpane {
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        setPrefSize(AppConfig.Window.prefHeight.toDouble(), AppConfig.Window.prefWidth.toDouble())
        content = gridpane {
            isPannable = true
            for (y in 0..graph.height * 2) {
                row {
                    for (x in 0..graph.width * 2) {
                        when {
                            y % 2 == 0 && x % 2 == 0 -> {
                                rectangle(width = AppConfig.GridPane.baseWidth, height = AppConfig.GridPane.baseWidth) { fill = Color.BLACK }
                            }
                            y % 2 == 0 -> {
                                hWall(this, x, y)
                            }
                            x % 2 == 0 -> {
                                vWall(this, x, y)
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

    private fun hWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = AppConfig.GridPane.baseLength, height = AppConfig.GridPane.baseWidth).apply {
                fill = when {
                    y == 0 || y == graph.height * 2 -> Color.BLACK
                    graph.walls[x / 2][y / 2].up == SolvedWall.WallState.WALL -> Color.BLACK
                    graph.walls[x / 2][y / 2].up == SolvedWall.WallState.ON_PATH -> Color.LIGHTGREEN
                    else -> Color.WHITE
                }
            }
        }
    }

    private fun vWall(pane: Pane, x: Int, y: Int) {
        pane.apply {
            rectangle(width = AppConfig.GridPane.baseWidth, height = AppConfig.GridPane.baseLength).apply {
                fill = when {
                    x == 0 || x == graph.width * 2 -> Color.BLACK
                    graph.walls[x / 2][y / 2].left == SolvedWall.WallState.WALL -> Color.BLACK
                    graph.walls[x / 2][y / 2].left == SolvedWall.WallState.ON_PATH -> Color.LIGHTGREEN
                    else -> Color.WHITE
                }
            }
        }
    }

    private fun tile(pane: Pane, x: Int, y: Int) {
        pane.apply {
            label {
                GridPane.setHalignment(this, HPos.CENTER)
                prefHeight = AppConfig.GridPane.baseLength.toDouble()
                prefWidth = AppConfig.GridPane.baseLength.toDouble()
                alignment = Pos.CENTER
                font = AppConfig.font
                val node = graph.grid[x / 2][y / 2]
                text = when {
                    node.isStart -> "S"
                    node.isEnd -> "F"
                    else -> node.value.toString()
                }
                background = if (node.isOnPath) {
                    Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))
                } else {
                    Background.EMPTY
                }
            }
        }
    }
}