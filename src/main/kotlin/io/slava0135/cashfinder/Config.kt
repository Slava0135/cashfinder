package io.slava0135.cashfinder

import javafx.scene.text.Font

object AppConfig {

    val font = Font.font(20.0)

    object GridPane {
        val baseLength = 64
        val baseWidth = 8
    }

    object Window {
        val prefHeight = 450
        val prefWidth = 800
    }
}