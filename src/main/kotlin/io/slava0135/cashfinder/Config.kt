package io.slava0135.cashfinder

import javafx.scene.text.Font

object AppConfig {

    val font: Font = Font.font(20.0)

    object GridPane {
        const val baseLength = 64
        const val baseWidth = 8
    }

    object Window {
        const val prefHeight = 450
        const val prefWidth = 800
    }
}