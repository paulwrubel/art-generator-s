package me.paul.artgenerators

import scala.swing._
import scala.swing.event._

object ArtGeneratorSApp extends SimpleSwingApplication {

    def top = new MainFrame {

        title = f"Art Generator S - ${Parameters.Version}"
        size = new Dimension(500,500)

        val startButton = new Button {
            text = "START"
        }

        val trashButton = new Button {
            text = "TRASH"
        }

        val startButtonLabel = new Label {
            text = "Start Generation: "
        }

        val trashButtonLabel = new Label {
            text = "            "
        }

        contents = new BoxPanel(Orientation.Horizontal){

            contents += startButtonLabel
            contents += startButton

            contents += trashButton
            contents += trashButtonLabel

            border = Swing.EmptyBorder
        }

        listenTo(startButton)
        listenTo(trashButton)
        reactions += {
            case ButtonClicked(`startButton`) =>
                Generator.startGeneration(Parameters.ImageCount)
            case ButtonClicked(`trashButton`) =>
                trashButtonLabel.text = "test"
        }

    }

}
