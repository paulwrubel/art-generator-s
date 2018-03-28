package me.paul.artgenerators

import java.awt.Color

import javax.swing.{UIManager, UnsupportedLookAndFeelException}
import javax.swing.text.{AbstractDocument, AttributeSet, DocumentFilter}
import javax.swing.text.DocumentFilter.FilterBypass

import scala.swing.GridBagPanel.{Anchor, Fill}
import scala.swing._
import scala.swing.event._

object ArtGeneratorSwingApp extends SimpleSwingApplication {

    try {
        // Set System L&F
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    } catch {
        case ue: UnsupportedLookAndFeelException =>
            ue.printStackTrace()
        case cnre: ClassNotFoundException =>
            cnre.printStackTrace()
        case ie: InstantiationException =>
            ie.printStackTrace()
        case iae: IllegalAccessException =>
            iae.printStackTrace()
        case e: Exception =>
            e.printStackTrace()
    }

    var canStart = true

    /* Component creation */

    val attributeLabel = new Label("Attribute")
    val valueLabel = new Label("Value")
    val feedbackLabel = new Label("Feedback")

    val imageWidthTextBoxLabel = new Label("Width: ")
    val imageHeightTextBoxLabel = new Label("Height: ")
    val imageWidthTextBox, imageHeightTextBox = new TextField {

        object IntegralFilter extends DocumentFilter {
            override def insertString(fb: FilterBypass, offs: Int, str: String, a: AttributeSet): Unit = {
                if ( str.forall( c => c.isDigit) )
                    super.insertString(fb, offs, str, a)
            }
            override def replace(fb: FilterBypass, offs: Int, l: Int, str: String, a: AttributeSet): Unit = {
                if ( str.forall( c => c.isDigit) )
                    super.replace(fb, offs, l, str, a)
            }
        }

        peer.getDocument.asInstanceOf[AbstractDocument].setDocumentFilter(IntegralFilter)

        text = ""
        maximumSize = new Dimension(150, 30)
        minimumSize = new Dimension(150, 30)
        preferredSize = new Dimension(150, 30)
    }

    val imageWidthFeedbackLabel = new Label
    val imageHeightFeedbackLabel = new Label

    val openFileCheckBoxLabel = new Label("Open File[s] After Generation?: ")
    val openFileCheckBox: CheckBox = new CheckBox {
        selected = DefaultParameters.OpenFile
    }
    val openFileFeedbackLabel = new Label

    val startButtonLabel = new Label("Start Generation: ")
    val startButton = new Button("START")

    /* MainFrame component layout */

    def top: MainFrame = new MainFrame {

        title = f"Art Generator S - ${Parameters.Version}"

        contents = new GridBagPanel() {

            val c = new Constraints

            // Main labels

            c.grid = (0, 0)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(attributeLabel) = c

            c.grid = (1, 0)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(valueLabel) = c

            c.grid = (2, 0)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(feedbackLabel) = c

            // Width

            c.grid = (0, 1)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageWidthTextBoxLabel) = c

            c.grid = (1, 1)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageWidthTextBox) = c

            c.grid = (2, 1)
            c.weightx = 0.0
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageWidthFeedbackLabel) = c

            // Height

            c.grid = (0, 2)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageHeightTextBoxLabel) = c

            c.grid = (1, 2)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageHeightTextBox) = c

            c.grid = (2, 2)
            c.weightx = 0.0
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(imageHeightFeedbackLabel) = c

            // Open File Checkbox

            c.grid = (0, 3)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(openFileCheckBoxLabel) = c

            c.grid = (1, 3)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(openFileCheckBox) = c

            c.grid = (2, 3)
            c.weightx = 0.0
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(openFileFeedbackLabel) = c

            // Start Button

            c.grid = (1, 4)
            c.weightx = 0.5
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(startButtonLabel) = c

            c.grid = (2, 4)
            c.weightx = 0.0
            c.anchor = Anchor.LineStart
            c.fill = Fill.None
            layout(startButton) = c

            // Other

            border = Swing.EmptyBorder(10)
        }

        // size = new Dimension(500,500)

        peer.setLocationRelativeTo(null)
    }

    /* Event listeners */

    val publishers = List(
        startButton,
        openFileCheckBox,
        imageWidthTextBox,
        imageHeightTextBox
    )

    listenTo(publishers: _*)

    reactions += {
        case ButtonClicked(`startButton`) =>
            if (canStart)
                Generator.startGeneration(Parameters.ImageCount)
            else
                println()
                // warning: fix errors
        case ButtonClicked(`openFileCheckBox`) =>
            if (openFileCheckBox.selected) {
                Parameters.OpenFile = true
                openFileFeedbackLabel.foreground = Color.GREEN
                openFileFeedbackLabel.text = "File[s] will be opened"
            } else {
                Parameters.OpenFile = false
                openFileFeedbackLabel.foreground = Color.RED
                openFileFeedbackLabel.text = "File[s] will NOT be opened"
            }
            println("Parameters.OpenFile = " + Parameters.OpenFile)
        case EditDone(`imageWidthTextBox`) =>
            val text = imageWidthTextBox.text

            if (text.length > 0 && text.length <= 9) {
                Parameters.Width = text.toInt
            } else if (text.length > 9) {
                imageWidthTextBox.border = Swing.LineBorder(Color.RED, 2)
                Parameters.Width = DefaultParameters.MaxWidth
            } else {
                Parameters.Width = DefaultParameters.Width
            }

            println("Parameters.Width = " + Parameters.Width)
        case EditDone(`imageHeightTextBox`) =>
            val text = imageHeightTextBox.text
            val value =
                if (text.length > 0)
                    text.toInt
                else
                    DefaultParameters.Height

            Parameters.Height = value
            println("Parameters.Height = " + Parameters.Height)

    }

}
