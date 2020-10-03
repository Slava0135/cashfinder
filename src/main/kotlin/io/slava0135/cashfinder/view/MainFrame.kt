package io.slava0135.cashfinder.view

import javafx.stage.FileChooser
import tornadofx.*

class Cashfinder: App(MainView::class)

class MainView: View() {
    override val root = borderpane {
        top<Menu>()
        bottom<Workspace>()
    }
}

class Menu: View() {
    override val root = menubar {
        menu("File") {
            item("New")
            item("Save").action {
                val file = chooseFile("Select Output File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")), mode = FileChooserMode.Save)
            }
            item("Load").action {
                val file = chooseFile("Select Input File", arrayOf(FileChooser.ExtensionFilter("Cash File (*.csh)", "*.csh")))
            }
        }
        menu("Solve")
        menu("Help")
    }
}

class Workspace: View() {
    override val root = label("WORKSPACE")
}