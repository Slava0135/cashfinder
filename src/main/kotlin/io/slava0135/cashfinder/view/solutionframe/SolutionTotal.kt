package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class SolutionTotal(graph: SolvedGraph) : Fragment() {

    val solution = graph.solution

    override val root = vbox {
        form {
            fieldset {
                field("Initial money: ${solution.initial}")
                if (solution.score != null) {
                    field("Total money: ${solution.score}")
                    field("Path length: ${solution.length}")
                    separator()
                    button("Save solution").action {
                        useMaxWidth = true
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
                    field("Solution is not found")
                }
            }
        }
    }
}