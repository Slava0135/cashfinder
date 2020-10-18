package io.slava0135.cashfinder.view.mainframe.menubar

import io.slava0135.cashfinder.AppConfig
import javafx.geometry.Pos
import tornadofx.*

class About: Fragment() {

    override val root = vbox {
        label("What is this program about?").apply { font = AppConfig.font; alignment = Pos.TOP_CENTER }
        separator()
        label("You are given a maze consisting for rooms.")
        label("If you enter ones with a positive value you will gain money. And if you enter ones with a negative value - you loose them.")
        label("The problem is how to get as many coins as possible while starting and finishing within a certain rooms and not waste all money")
    }

}