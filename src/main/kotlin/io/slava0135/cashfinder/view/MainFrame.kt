package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.Graph
import io.slava0135.cashfinder.model.Node
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import java.io.File
import java.lang.IllegalStateException

private val graph = objectProperty<Graph>()
private val start = mutableSetOf<Node>()
private val end = mutableSetOf<Node>()

private fun fixGraph() {
    if (graph.value == null) throw IllegalStateException("No graph is present")
    if (start.size == 0) throw IllegalStateException("No start is present")
    if (end.size == 0) throw IllegalStateException("No end is present")
    if (start.size > 1) throw IllegalArgumentException("Multiple start tiles")
    if (end.size > 1) throw IllegalArgumentException("Multiple finish tiles")
    graph.value.start = start.first()
    graph.value.end = end.first()
}

private fun save(file: File) {
    file.writeText(graph.value.toString())
}

private fun load(file: File) {
    graph.value = Graph.createFromLines(file.readLines())
    start.add(graph.value.start!!)
    end.add(graph.value.end!!)
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
                confirm("Are you sure?", "Current grid will be deleted!") {
                    CreationMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                }
            }
            item("Save").action {
                try {
                    fixGraph()
                    val files = chooseFile("Select Output File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")), mode = FileChooserMode.Save)
                    if (files.isNotEmpty()) {
                        save(files.first())
                    }
                } catch (e: Exception) {
                    error(e.localizedMessage)
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
                                        fill = if (y == 0 || y == grid[0].size * 2 || walls[x / 2][y / 2].up) Color.BLACK else Color.WHITE
                                        setOnMouseClicked {
                                            if (y != 0 && y != grid[0].size * 2) {
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
                                x % 2 == 0 -> {
                                    rectangle(width = 8, height = 64) { fill = Color.BLACK }.apply {
                                        fill = if (x == 0 || x == grid.size * 2 || walls[x / 2][y / 2].left) Color.BLACK else Color.WHITE
                                        setOnMouseClicked {
                                            if (x != 0 && x != grid.size * 2) {
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
                                else -> {
                                    textfield {
                                        maxWidth = 64.0
                                        alignment = Pos.CENTER
                                        background = Background.EMPTY
                                        font = Font.font(20.0)
                                        val elem = grid[x / 2][y / 2]
                                        text = when {
                                            elem.isStart -> "S"
                                            elem.isEnd -> "F"
                                            else -> elem.value.toString()
                                        }
                                        filterInput {
                                            it.controlNewText.let {
                                                it in listOf("S", "F", "-") || it.isInt() && it.toInt() in -99..99
                                            }
                                        }
                                        setOnKeyReleased {
                                            when (text) {
                                                "S" -> {
                                                    val node = graph.value.grid[x / 2][y / 2]
                                                    if (node.isEnd) end.remove(node)
                                                    node.apply {
                                                        value = 0
                                                        isStart = true
                                                        isEnd = false
                                                    }
                                                    start.add(node)
                                                }
                                                "F" -> {
                                                    val node = graph.value.grid[x / 2][y / 2]
                                                    if (node.isStart) start.remove(node)
                                                    node.apply {
                                                        value = 0
                                                        isStart = false
                                                        isEnd = true
                                                    }
                                                    end.add(node)
                                                }
                                                else -> {
                                                    val node = graph.value.grid[x / 2][y / 2]
                                                    if (node.isEnd) end.remove(node)
                                                    if (node.isStart) start.remove(node)
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
                    }
                }
            }
        }
    }
}