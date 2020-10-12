package io.slava0135.cashfinder

import javafx.scene.text.Font

object AppConfig {

    val font: Font = Font.font(20.0)

    object Graph {
        const val sizeLimit = 50
        const val randomLimit = 25
        const val valueLimit = 99
    }

    object GridPane {
        const val baseLength = 64
        const val baseWidth = 8
    }

    object Window {
        const val prefHeight = 450
        const val prefWidth = 800
    }
}