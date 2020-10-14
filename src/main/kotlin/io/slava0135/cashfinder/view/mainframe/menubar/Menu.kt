package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.view.mainframe.graph
import io.slava0135.cashfinder.view.mainframe.load
import io.slava0135.cashfinder.view.mainframe.save
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class Menu: View() {
    override val root = menubar {
        menu("File") {
            item("New").action {
                confirm("Are you sure?", "Current grid will be deleted!") {
                    CreationMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                }
            }
            separator()
            item("Save").action {
                try {
                    if (graph.value == null) throw IllegalStateException("No Graph is found")
                    graph.value.validate()
                    val files =
                            chooseFile(
                                    "Select Output File",
                                    arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")), mode = FileChooserMode.Save)
                    if (files.isNotEmpty()) {
                        graph.value.save(files.first())
                    }
                } catch (e: Exception) {
                    error(e.localizedMessage)
                }
            }
            item("Load").action {
                confirm("Are you sure?", "Current grid will be deleted!") {
                    val files =
                            chooseFile(
                                    "Select Input File",
                                    arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")))
                    if (files.isNotEmpty()) {
                        if (graph.value == null) {
                            try {
                                Graph.load(files.first())
                            } catch (e: Exception) {
                                error(e.localizedMessage)
                            }
                        }
                    }
                }
            }
        }
        menu("Edit") {
            item("Change size")
        }
        menu("Solve") {
            item("Solve").action {
                try {
                    if (graph.value == null) throw IllegalStateException("No Graph is found")
                    graph.value.validate()
                    SolutionMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                } catch(e: Exception) {
                    error(e.localizedMessage)
                }
            }
            separator()
            item("Import Solution File")
        }
        menu("Help") {
            item("About the program")
        }
    }
}