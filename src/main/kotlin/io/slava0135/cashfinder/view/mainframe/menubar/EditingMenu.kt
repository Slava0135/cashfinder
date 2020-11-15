package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.view.mainframe.graph
import tornadofx.*

class EditingMenu: Fragment("Change Size") {

    var up = 0
    var down = 0
    var right = 0
    var left = 0

    var allWalls = true
    var random = false

    override val root = hbox {
        form {
            fieldset {
                field("Up") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it == "-" || it.isInt()
                            }
                        }
                        setOnKeyReleased {
                            if (this.text.isInt()) {
                                up = this.text.toInt()
                            }
                        }
                    }
                }
                separator()
                field("Down") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it == "-" || it.isInt()
                            }
                        }
                        setOnKeyReleased {
                            if (this.text.isInt()) {
                                down = this.text.toInt()
                            }
                        }
                    }
                }
                separator()
                field("Right") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it == "-" || it.isInt()
                            }
                        }
                        setOnKeyReleased {
                            if (this.text.isInt()) {
                                right = this.text.toInt()
                            }
                        }
                    }
                }
                separator()
                field("Left") {
                    textfield {
                        filterInput {
                            it.controlNewText.let {
                                it == "-" || it.isInt()
                            }
                        }
                        setOnKeyReleased {
                            if (this.text.isInt()) {
                                left = this.text.toInt()
                            }
                        }
                    }
                }
                separator()
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
                separator()
                button("Apply") {
                    useMaxWidth = true
                    shortcut("Enter")
                    action {
                        try {
                            val new = graph.value.changeSize(up = up, down = down, right = right, left = left, allWalls = allWalls, random = random)
                            graph.value = new
                            close()
                        } catch(e: Exception) {
                            error(e.localizedMessage)
                        }
                    }
                }
            }
        }
    }
}