package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.Graph
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.effect.BlendMode
import javafx.scene.layout.Background
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import java.io.File
import java.lang.IllegalStateException

var graph = objectProperty<Graph>()

fun save(file: File) {
    if (graph.value == null) throw IllegalStateException("No graph is present")
    file.writeText(graph.toString())
}

fun load(file: File) {
    graph.value = Graph.createFromLines(file.readLines())
}

class Cashfinder: App(MainView::class)

class MainView: View() {
    override val root = borderpane {
        top<Menu>()
        center<Workspace>()
    }
}

class Menu: View() {
    override val root = menubar {
        menu("File") {
            item("New").action {
                if (graph.value == null) {
                    CreationMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                } else {
                    confirm("Are you sure?", "Current grid will be deleted!") {
                        CreationMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                    }
                }
            }
            item("Save").action {
                val files = chooseFile("Select Output File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")), mode = FileChooserMode.Save)
                if (files.isNotEmpty()) {
                    try {
                        save(files.first())
                    } catch (e: Exception) {
                        error(e.localizedMessage)
                    }
                }
            }
            item("Load").action {
                confirm("Are you sure?", "Current grid will be deleted!") {
                    val files = chooseFile("Select Input File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")))
                    if (files.isNotEmpty()) {
                        if (graph.value == null) {
                            try {
                                load(files.first())
                            } catch (e: Exception) {
                                error(e.localizedMessage)
                            }
                        }
                    }
                }
            }
        }
        menu("Solve").action {

        }
        menu("Help").action {

        }
    }
}

class CreationMenu: Fragment() {

    var graphHeight: TextField by singleAssign()
    var graphWidth: TextField by singleAssign()

    override val root = hbox {
        form {
            fieldset("Create new Graph") {
                field("Width") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it.isInt() && it.toInt() in 1..50
                            }
                        }
                        graphWidth = this
                    }
                }
                field("Height") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it.isInt() && it.toInt() in 1..50
                            }
                        }
                        graphHeight = this
                    }
                }
                button("Create") {
                    useMaxWidth = true
                    action {
                        if (graphHeight.text.isNotEmpty() && graphWidth.text.isNotEmpty()) {
                            val width = graphWidth.text.toInt()
                            val height = graphHeight.text.toInt()
                            if (width > 0 && height > 0) {
                                graph.value = Graph.createEmpty(width, height)
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}

class Workspace: Fragment() {

    init {
        graph.addListener {
            _, _, _ -> replaceWith<Workspace>()
        }
    }

    override val root = scrollpane {
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        setMinSize(800.0, 450.0)
        content = gridpane {
            isPannable = true
            if (graph.value != null) {
                val grid = graph.value.grid
                val walls = graph.value.walls
                for (y in 0..grid[0].size * 2) {
                    row {
                        for (x in 0..grid.size * 2) {
                            when {
                                y % 2 == 0 && x % 2 == 0 -> {
                                    rectangle(width = 8, height = 8) { fill = Color.BLACK }
                                }
                                y % 2 == 0 -> {
                                    rectangle(width = 64, height = 8) { fill = Color.BLACK }.apply {
                                        fill = if (y == 0 || y == grid[0].size * 2 || walls[x % 2][y % 2].down) Color.BLACK else Color.WHITE
                                        setOnMouseClicked {
                                            fill = if (y != 0 && y != grid[0].size * 2 && fill == Color.BLACK) Color.WHITE
                                            else Color.BLACK
                                        }
                                    }
                                }
                                x % 2 == 0 -> {
                                    rectangle(width = 8, height = 64) { fill = Color.BLACK }.apply {
                                        fill = if (x == 0 || x == grid.size * 2 || walls[x % 2][y % 2].right) Color.BLACK else Color.WHITE
                                        setOnMouseClicked {
                                            fill = if (x != 0 && x != grid.size * 2 && fill == Color.BLACK) Color.WHITE
                                            else Color.BLACK
                                        }
                                    }
                                }
                                else -> {
                                    textfield {
                                        maxWidth = 64.0
                                        alignment = Pos.CENTER
                                        background = Background.EMPTY
                                        font = Font.font(20.0)
                                        text = grid[x % 2][y % 2].value.toString()
                                        filterInput {
                                            it.controlNewText.let {
                                                it in listOf("S", "F") || it.isInt() && it.toInt() in -99..99
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
    }
}