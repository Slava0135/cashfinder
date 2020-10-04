package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.Graph
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
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
        minWidth = 800.0
        minHeight = 450.0
        top<Menu>()
        bottom<Workspace>()
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
                                it.isInt() && it.toInt() in 1..100
                            }
                        }
                        graphWidth = this
                    }
                }
                field("Height") {
                    textfield {
                        filterInput { it.controlNewText.isInt() }
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
            _, _, newValue -> changeView()
        }
    }

    private fun changeView() {
        find<MainView>().root.bottom = Workspace().root
    }

    override val root = gridpane {
        if (graph.value != null) {
            val grid = graph.value.grid
            val walls = graph.value.walls
            for (x in 0..grid.size * 2) {
                for (y in 0..grid[0].size * 2) {
                    when {
                        y % 2 == 0 && x % 2 == 0 -> {
                            this.add(rectangle(width = 8, height = 8), x, y)
                        }
                        y % 2 == 0 -> {
                            this.add(rectangle(width = 32, height = 8), x, y)
                        }
                        x % 2 == 0 -> {
                            this.add(rectangle(width = 8, height = 32), x, y)
                        }
                        else -> {
                            this.add(textfield(), x, y)
                        }
                    }
                }
            }
        }
    }
}