package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.SolvedGraph
import io.slava0135.cashfinder.model.SolvedWall
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class SolutionFrame(private val graph: SolvedGraph) : Fragment("Solution") {
    override val root = borderpane {
        right<SolutionTotal>()
        left = SolutionView(graph).root
    }
}

class SolutionTotal : Fragment() {
    override val root = vbox {

    }
}

class SolutionView(private val graph: SolvedGraph) : Fragment() {
    override val root = scrollpane {
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        setPrefSize(800.0, 450.0)
        content = gridpane {
            isPannable = true
            for (y in 0..graph.height * 2) {
                row {
                    for (x in 0..graph.width * 2) {
                        when {
                            y % 2 == 0 && x % 2 == 0 -> {
                                rectangle(width = 8, height = 8) { fill = Color.BLACK }
                            }
                            y % 2 == 0 -> {
                                rectangle(width = 64, height = 8).apply {
                                    fill = when {
                                        y == 0 || y == graph.height * 2 -> Color.BLACK
                                        graph.walls[x / 2][y / 2].up == SolvedWall.WallState.WALL -> Color.BLACK
                                        graph.walls[x / 2][y / 2].up == SolvedWall.WallState.ON_PATH -> Color.LIGHTGREEN
                                        else -> Color.WHITE
                                    }
                                }
                            }
                            x % 2 == 0 -> {
                                rectangle(width = 8, height = 64).apply {
                                    fill = when {
                                        x == 0 || x == graph.width * 2 -> Color.BLACK
                                        graph.walls[x / 2][y / 2].left == SolvedWall.WallState.WALL -> Color.BLACK
                                        graph.walls[x / 2][y / 2].left == SolvedWall.WallState.ON_PATH -> Color.LIGHTGREEN
                                        else -> Color.WHITE
                                    }
                                }
                            }
                            else -> {
                                label {
                                    GridPane.setHalignment(this, HPos.CENTER)
                                    prefHeight = 64.0
                                    prefWidth = 64.0
                                    alignment = Pos.CENTER
                                    font = Font.font(20.0)
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
                }
            }
        }
    }
}