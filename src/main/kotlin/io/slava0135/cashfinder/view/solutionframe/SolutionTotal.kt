package io.slava0135.cashfinder.view.solutionframe

import io.slava0135.cashfinder.AppConfig
import io.slava0135.cashfinder.model.Solution
import tornadofx.*

class SolutionTotal(solution: Solution) : Fragment() {
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