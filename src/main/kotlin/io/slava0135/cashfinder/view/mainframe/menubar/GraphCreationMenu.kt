package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.graph.Graph
import io.slava0135.cashfinder.view.mainframe.graph
import javafx.scene.control.TextField
import tornadofx.*

class GraphCreationMenu: Fragment("New") {

    var graphHeight: TextField by singleAssign()
    var graphWidth: TextField by singleAssign()

    private enum class Generator(val type: String) {
        EMPTY("No walls"),
        ALL("All walls"),
        RANDOM("Randomized");

        override fun toString() = type
    }

    private val comboBox = combobox<Generator> {
        items.setAll(*Generator.values())
        setOnAction {
            gotGenerator = selectedItem != null
        }
    }

    var random = false

    var gotGenerator = false
        set(value) {
            gotEverything.value = value && gotHeight && gotWidth
            field = value
        }
    var gotWidth = false
        set(value) {
            gotEverything.value = value && gotHeight && gotGenerator
            field = value
        }
    var gotHeight = false
        set(value) {
            gotEverything.value = value && gotWidth && gotGenerator
            field = value
        }

    var gotEverything = booleanProperty()

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
                field("Assign random values")  {
                    checkbox {
                        action {
                            random = isSelected
                        }
                    }
                }
                field("Walls:")  {
                    add(comboBox)
                }
                button("Create!") {
                    useMaxWidth = true
                    enableWhen(gotEverything)
                    action {
                        if (graphHeight.text.isNotEmpty() && graphWidth.text.isNotEmpty()) {
                            val width = graphWidth.text.toInt()
                            val height = graphHeight.text.toInt()
                            if (width > 0 && height > 0) {
                                val g = Graph.createEmpty(width, height, random)
                                when (comboBox.selectedItem!!) {
                                    Generator.ALL -> g.generateAllWalls()
                                    Generator.RANDOM -> g.generateRandomWalls()
                                    else -> {}
                                }
                                graph.value = g
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}