package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph.Factory.load
import io.slava0135.cashfinder.view.mainframe.graph
import io.slava0135.cashfinder.view.mainframe.load
import io.slava0135.cashfinder.view.mainframe.save
import io.slava0135.cashfinder.view.solutionframe.SolutionFrame
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import java.io.File

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
                        graph.value.save(
                                if (files.first().absolutePath.endsWith(".csh")) files.first()
                                else File(files.first().canonicalPath + ".csh"))
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
                        try {
                            Graph.load(files.first())
                        } catch (e: Exception) {
                            error(e.localizedMessage)
                        }
                    }
                }
            }
        }
        menu("Edit") {
            item("Change size").action {
                try {
                    if (graph.value == null) throw IllegalStateException("No Graph is found")
                    EditingMenu().openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                } catch(e: Exception) {
                    error(e.localizedMessage)
                }
            }
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
            item("Import Solution File").action {
                val files =
                        chooseFile(
                                "Select Input File",
                                arrayOf(FileChooser.ExtensionFilter("Cash File (*.sol)", "*.sol")))
                if (files.isNotEmpty()) {
                    try {
                        SolutionFrame(load(files.first())).openWindow(StageStyle.UTILITY, Modality.NONE, true, block = true, resizable = false)
                    } catch (e: Exception) {
                        error(e.localizedMessage)
                    }
                }
            }
        }
        menu("Help") {
            item("About the program")
        }
    }
}