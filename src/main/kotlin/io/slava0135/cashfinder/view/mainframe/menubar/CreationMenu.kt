package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.view.mainframe.graph
import javafx.scene.control.TextField
import tornadofx.*

class CreationMenu: Fragment("New") {

    var graphHeight: TextField by singleAssign()
    var graphWidth: TextField by singleAssign()
    var allWalls = true
    var random = false

    var gotWidth = false
        set(value) {
            gotEverything.value = value && gotHeight
            field = value
        }
    var gotHeight = false
        set(value) {
            gotEverything.value = value && gotWidth
            field = value
        }
    var gotEverything = booleanProperty(false)

    override val root = hbox {
        form {
            fieldset("Create a new Graph") {
                field("Width") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it.isInt() && it.toInt() in 1..AppConfig.Graph.sizeLimit
                            }
                        }
                        setOnKeyReleased {
                            gotWidth = (text.isInt() && text.toInt() in 1..AppConfig.Graph.sizeLimit)
                        }
                        graphWidth = this
                    }
                }
                field("Height") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it.isInt() && it.toInt() in 1..AppConfig.Graph.sizeLimit
                            }
                        }
                        setOnKeyReleased {
                            gotHeight = (text.isInt() && text.toInt() in 1..AppConfig.Graph.sizeLimit)
                        }
                        graphHeight = this
                    }
                }
                field("Place walls everywhere")  {
                    checkbox {
                        isSelected = true
                        action {
                            allWalls = isSelected
                        }
                    }
                }
                field("Assign random values")  {
                    checkbox {
                        action {
                            random = isSelected
                        }
                    }
                }
                button("Create!") {
                    useMaxWidth = true
                    enableWhen(gotEverything)
                    action {
                        if (graphHeight.text.isNotEmpty() && graphWidth.text.isNotEmpty()) {
                            val width = graphWidth.text.toInt()
                            val height = graphHeight.text.toInt()
                            if (width > 0 && height > 0) {
                                graph.value = Graph.createEmpty(width, height, allWalls, random)
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}