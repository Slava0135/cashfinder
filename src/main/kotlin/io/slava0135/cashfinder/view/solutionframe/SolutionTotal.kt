package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.Solution
import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import io.slava0135.cashfinder.view.mainframe.save
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class SolutionTotal(graph: SolvedGraph) : Fragment() {

    val solution = graph.solution

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
                        button("Save solution").action {
                            val files =
                                    chooseFile(
                                            "Select Output File",
                                            arrayOf(FileChooser.ExtensionFilter("Cash File (*.sol)", "*.sol")), mode = FileChooserMode.Save)
                            if (files.isNotEmpty()) {
                                graph.save(
                                        if (files.first().absolutePath.endsWith(".sol")) files.first()
                                        else File(files.first().canonicalPath + ".sol"))
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