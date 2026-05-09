package io.github.osdanova.ffxprojecteditor

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class App : Application() {
    override fun start(stage: Stage) {
        stage.title = "FFX Project Editor"
        stage.scene = Scene(StackPane(Label("FFX Project Editor — Kotlin port (phase 1)")), 800.0, 450.0)
        stage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
