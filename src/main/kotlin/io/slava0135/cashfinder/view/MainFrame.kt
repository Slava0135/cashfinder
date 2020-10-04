package io.slava0135.cashfinder.view

import io.slava0135.cashfinder.model.Graph
import io.slava0135.cashfinder.model.Model
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import tornadofx.*

class Cashfinder: App(MainView::class)

class MainView: View() {
    override val root = borderpane {
        top<Menu>()
        bottom<Workspace>()
    }
}

class Menu: View() {
    override val root = menubar {
        menu("File") {
            item("New").action {
                if (Model.graph == null) {
                    openInternalWindow(CreationMenu())
                } else {
                    confirm("Are you sure?", "Current grid will be deleted!") {
                        openInternalWindow(CreationMenu())
                    }
                }
            }
            item("Save").action {
                val files = chooseFile("Select Output File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")), mode = FileChooserMode.Save)
                if (files.isNotEmpty()) {
                    try {
                        Model.save(files.first())
                    } catch (e: Exception) {
                        error(e.localizedMessage)
                    }
                }
            }
            item("Load").action {
                confirm("Are you sure?", "Current grid will be deleted!") {
                    val files = chooseFile("Select Input File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")))
                    if (files.isNotEmpty()) {
                        if (Model.graph == null) {
                            try {
                                Model.load(files.first())
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

class Workspace: View() {
    override val root = label("WORKSPACE")
}

class CreationMenu: View() {

    var graphHeight: TextField by singleAssign()
    var graphWidth: TextField by singleAssign()

    override val root = vbox {
        hbox {
            label("Width")
            val field = textfield()
            field.filterInput { it.controlNewText.isInt() }
            graphWidth = field
        }
        hbox {
            label("Height")
            val field = textfield()
            field.filterInput { it.controlNewText.isInt() }
            graphHeight = field
        }
        button("Create") {
            useMaxWidth = true
            action {
                if (graphHeight.text.isNotEmpty() && graphWidth.text.isNotEmpty()) {
                    Model.graph = Graph.createEmpty(graphWidth.text.toInt(), graphHeight.text.toInt())
                    close()
                }
            }
        }
    }
}