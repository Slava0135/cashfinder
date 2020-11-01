package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.model.solvedgraph.SolvedGraph
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class SolutionTotal(graph: SolvedGraph) : Fragment() {

    override val root = vbox {
        form {
            fieldset {

                field("Initial money: ${graph.initial}")

                if (graph.score != null) {

                    field("Total money: ${graph.score}")

                    field("Path length: ${graph.length}")

                    separator()

                    button("Save the Solution").action {
                        useMaxWidth = true
                        val files = chooseFile(
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