package io.slava0135.cashfinder.view

import tornadofx.*

class Cashfinder: App(MainView::class)

class MainView: View() {
    override val root = borderpane {
        top<TopView>()
        bottom<BottomView>()
    }
}

class TopView: View() {
    override val root = menubar {
        menu("File") {
            item("New")
            item("Save")
            item("Load")
        }
        menu("Solve")
        menu("Help")
    }
}

class BottomView: View() {
    override val root = label("WORKSPACE")
}