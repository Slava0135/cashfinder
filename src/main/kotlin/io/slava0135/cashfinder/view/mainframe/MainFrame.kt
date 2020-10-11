package io.slava0135.cashfinder.view.mainframe

import io.slava0135.cashfinder.view.mainframe.menubar.Menu
import io.slava0135.cashfinder.view.mainframe.workspace.Workspace
import tornadofx.*

class Cashfinder: App(MainView::class)

class MainView: View("Cashfinder") {
    override val root = borderpane {
        top<Menu>()
        center<Workspace>()
    }
}