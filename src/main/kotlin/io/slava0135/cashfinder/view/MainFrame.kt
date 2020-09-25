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
    override val root = label("Top View")
}

class BottomView: View() {
    override val root = label("Bottom View")
}