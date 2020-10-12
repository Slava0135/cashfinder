package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.model.Solver
import io.slava0135.cashfinder.view.mainframe.graph
import io.slava0135.cashfinder.view.solutionframe.SolutionFrame
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class SolutionMenu: Fragment("Find a Solution") {

    private val comboBox = combobox<Solver> {
        items.setAll(*Solver.values())
        setOnAction {
            isSelected.value = selectedItem != null
        }
    }
    var isSelected = booleanProperty()
    var initial: TextField by singleAssign()

    override val root = hbox {
        form {
            fieldset("Solve the Graph") {
                field("Initial money") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it.isInt() && it.toInt() >= 0
                            }
                        }
                        text = "0"
                        initial = this
                    }
                }
                field("Choose the algorithm") {
                    add(comboBox)
                }
                button("Solve!") {
                    useMaxWidth = true
                    enableWhen(isSelected)
                    action {
                        if (initial.text.isEmpty()) {
                            initial.text = "0"
                        }
                        SolutionFrame(comboBox.selectedItem!!.solve(graph.value, initial.text.toInt()))
                                .openWindow(StageStyle.UTILITY, Modality.NONE, true, block = false, resizable = true)
                        close()
                    }
                }
            }
        }
    }
}