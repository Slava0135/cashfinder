package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.AppConfig
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import tornadofx.*

class About : Fragment() {

    override val root = form {
        background = Background(BackgroundFill(javafx.scene.paint.Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
        label("What is this program about?").apply { font = AppConfig.font }
        separator()
        label("You are given a maze consisting of rooms.")
        label("If you enter ones with a positive value you will gain money. And if you enter ones with a negative value - you loose them.")
        label("The problem is how to get as many coins as possible while starting and finishing within a certain rooms and not waste all money.")
        separator()
        label("You can create mazes in both text editor and this program's workspace.")
        separator()
        hbox {
            prefHeight = 300.0
            prefWidth = 950.0
            spacing = 16.0
            vbox {
                imageview("about1.png")
                text("Text input").apply {
                    alignment = Pos.TOP_CENTER
                    font = AppConfig.font
                }
            }
            separator(Orientation.VERTICAL)
            vbox {
                imageview("about2.png")
                text("Program workspace").apply {
                    alignment = Pos.TOP_CENTER
                    font = AppConfig.font
                }
            }
            separator(Orientation.VERTICAL)
            vbox {
                imageview("about3.png")
                text("Solution view").apply {
                    alignment = Pos.TOP_CENTER
                    font = AppConfig.font
                }
            }
        }
    }
}