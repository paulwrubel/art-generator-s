package me.paul.artgenerators

import java.awt.{Color, Dimension, Font}
import java.io.File

import javax.swing.{UIManager, UnsupportedLookAndFeelException}
import javax.swing.text.{AbstractDocument, AttributeSet, DefaultCaret, DocumentFilter}
import javax.swing.text.DocumentFilter.FilterBypass

import scala.swing._
import scala.swing.event._

object ArtGeneratorSwingApp extends SimpleSwingApplication {

    var isRunning: Boolean = false

    val DefaultColor: Color = Color.BLUE
    val ValidColor: Color   = Color.GREEN
    val WarningColor: Color = Color.YELLOW
    val InvalidColor: Color = Color.RED

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

    /* ----- Component creation ----- */

    // Top Level labels

    val attributeLabel = new Label("Attribute")
    val valueLabel = new Label("Value")
    val feedbackLabel = new Label("Feedback")

    // width and height rows

    val imageWidthTextFieldLabel = new Label("Width: ")
    val imageHeightTextFieldLabel = new Label("Height: ")
    val imageWidthTextField, imageHeightTextField = new TextField {
        peer.getDocument.asInstanceOf[AbstractDocument].setDocumentFilter(IntegralFilter)
    }

    val imageWidthFeedbackLabel: Label = new Label {
        horizontalAlignment = Alignment.Left
        border = Swing.LineBorder(DefaultColor)
        text = f"Width: ${DefaultParameters.Width} [DEFAULT]"
    }
    val imageHeightFeedbackLabel: Label = new Label {
        horizontalAlignment = Alignment.Left
        border = Swing.LineBorder(DefaultColor)
        text = f"Height: ${DefaultParameters.Height} [DEFAULT]"
    }

    // open file check box

    val openFileCheckBoxLabel = new Label("Open File[s] After Generation?")
    val openFileCheckBox: CheckBox = new CheckBox {
        selected = DefaultParameters.OpenFile
    }
    val openFileFeedbackLabel: Label = new Label {
        horizontalAlignment = Alignment.Left
        border = Swing.LineBorder(DefaultColor)
        text = "File[s] will be opened [DEFAULT]"
    }

    // filename

    val filenameTextFieldLabel = new Label("Filename: ")
    val filenameTextField: TextField = new TextField
    val filenameFeedbackLabel: Label = new Label {
        horizontalAlignment = Alignment.Left
        border = Swing.LineBorder(DefaultColor)
        text = "Filename: " + "\"" + s"${DefaultParameters.Version}-${DefaultParameters.Width}x${DefaultParameters.Height}" + "\"" + " [DEFAULT]"
    }

    // file path

    val filepathFileChooserButtonLabel = new Label("Choose Filepath: ")
    val filepathFileChooser: FileChooser = new FileChooser {
        fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
        selectedFile = new File(new File(ArtGeneratorSwingApp.this.getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParent
                + s"/images/${DefaultParameters.Version}/${DefaultParameters.Width}x${DefaultParameters.Height}/")
    }
    val filepathFileChooserButton = new Button("CHOOSE FOLDER")
    val filepathFeedbackLabel: Label = new Label {
        horizontalAlignment = Alignment.Left
        border = Swing.LineBorder(DefaultColor)
        text = "Output Folder: "+ "\"" + s"${filepathFileChooser.selectedFile.getPath}" + "\"" + " [DEFAULT]"
    }

    // Image Count
    val imageCountTextFieldLabel = new Label("Number of Images: ")
    val imageCountTextField: TextField = new TextField {
        peer.getDocument.asInstanceOf[AbstractDocument].setDocumentFilter(IntegralFilter)
    }
    val imageCountFeedbackLabel: Label = new Label {
        border = Swing.LineBorder(DefaultColor)
        horizontalAlignment = Alignment.Left
        text = s"Number of Images: ${DefaultParameters.ImageCount} [DEFAULT]"
    }

    // TODO: ImageCount
    // TODO: SeedCount

    // TODO: HueVariation
    // TODO: SaturationVariation
    // TODO: BrightnessVariation

    // TODO: HueBounds
    // TODO: SaturationBounds
    // TODO: BrightnessBounds

    // start button

    val startButtonLabel = new Label("Start Generation: ")
    val startButton = new Button("START")

    // clear button

    val clearButtonLabel = new Label("Clear Console: ")
    val clearButton = new Button("CLEAR")

    // program output

    val output: TextArea = new TextArea {
        rows = 1
        columns = 100
        editable = false
        font = new Font(Font.MONOSPACED, Font.PLAIN, 14)
        peer.getCaret.asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)
    }

    // progress bar

    val singleImageProgressBar: ProgressBar = new ProgressBar {
        min = 0
        value = 0
        labelPainted = true
        font = new Font(Font.MONOSPACED, Font.PLAIN, 14)

        label = f"${0.asInstanceOf[Double]}%6.2f %%"
    }

    val multiImageProgressBar: ProgressBar = new ProgressBar {
        min = 0
        value = 0
        labelPainted = true
        font = new Font(Font.MONOSPACED, Font.PLAIN, 14)

        label = f"${0}%,4d Images Completed"
    }

    /* ----- MainFrame component layout ----- */

    def top: MainFrame = new MainFrame {

        title = f"Art Generator S - ${DefaultParameters.Version}"

        // App Layout
        contents = new GridBagPanel {

            val c = new Constraints()
            c.grid = (0, 0)
            c.insets = new Insets(5, 5, 5, 5)

            // For all settings
            c.weightx = 1
            c.weighty = 0.0
            c.fill = GridBagPanel.Fill.Both

            layout(new ScrollPane(

                // Settings Rows inc. Top Labels
                new GridBagPanel {

                    val c = new Constraints()
                    c.grid = (0, 0)
                    c.insets = new Insets(3, 3, 3, 3)

                    // Top labels for columns

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(attributeLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(valueLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.None
                    layout(feedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Separators
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.North
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(new Separator) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.North
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(new Separator) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.North
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(new Separator) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.North
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(Swing.HGlue) = c

                    // Width
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(imageWidthTextFieldLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(imageWidthTextField) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(imageWidthFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Height
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(imageHeightTextFieldLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(imageHeightTextField) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(imageHeightFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Open File Checkbox
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(openFileCheckBoxLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(openFileCheckBox) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(openFileFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Filename
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(filenameTextFieldLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(filenameTextField) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(filenameFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Filepath
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(filepathFileChooserButtonLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(filepathFileChooserButton) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(filepathFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Image count
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(imageCountTextFieldLabel) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.Horizontal
                    layout(imageCountTextField) = c

                    c.gridx += 1
                    c.weightx = 0
                    c.anchor = GridBagPanel.Anchor.West
                    c.fill = GridBagPanel.Fill.Both
                    layout(imageCountFeedbackLabel) = c

                    c.gridx += 1
                    c.weightx = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.HGlue) = c

                    // Final row glue
                    c.gridx = 0
                    c.gridy += 1

                    c.weightx = 0
                    c.weighty = 1
                    c.anchor = GridBagPanel.Anchor.Center
                    c.fill = GridBagPanel.Fill.None
                    layout(Swing.VGlue) = c


                    //minimumSize = new Dimension(800, 250)

                }
            ){
                // Scroll Panel settings
                minimumSize = new Dimension(800, 250)

            }) = c

            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(new Separator()) = c

            // Start button and label
            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(new FlowPanel {
                contents += startButtonLabel
                contents += startButton

                contents += Swing.HStrut(10)

                contents += clearButtonLabel
                contents += clearButton
            }) = c

            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(new Separator()) = c

            // Output scroll pane
            c.gridy += 1
            c.weighty = 0.5
            c.fill = GridBagPanel.Fill.Both
            layout(new ScrollPane(output){
                minimumSize = new Dimension(800, 20)
            }) = c

            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(new Separator()) = c

            // Progress Bars
            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(singleImageProgressBar{
                minimumSize = new Dimension(0, 50)
            }) = c

            c.gridy += 1
            c.weighty = 0
            c.fill = GridBagPanel.Fill.Horizontal
            layout(multiImageProgressBar) = c

            // Other

            border = Swing.EmptyBorder(10)
        }

        size = new Dimension(1000, 600)
        minimumSize = new Dimension(300, 300)

        peer.setLocationRelativeTo(null)
    }

    /* ----- Event listeners ----- */

    val publishers = List(
        startButton,
        clearButton,
        openFileCheckBox,
        imageWidthTextField,
        imageHeightTextField,
        filenameTextField,
        filepathFileChooserButton,
        imageCountTextField
    )

    listenTo(publishers: _*)

    reactions += {
        case ButtonClicked(`startButton`) =>
            if (!isRunning) {
                isRunning = true
                val params: Parameters = initializeParameters
                val gen = new Generator(params, output, singleImageProgressBar, multiImageProgressBar)
                gen.execute()
            } else {
                // warning: fix errors
            }
        case ButtonClicked(`clearButton`) =>
            output.text = ""
        case ButtonClicked(`openFileCheckBox`) =>
            if (openFileCheckBox.selected) {
                openFileFeedbackLabel.border = Swing.LineBorder(DefaultColor)
                openFileFeedbackLabel.text = "File[s] will be opened [DEFAULT]"
            } else {
                openFileFeedbackLabel.border = Swing.LineBorder(ValidColor)
                openFileFeedbackLabel.text = "File[s] will NOT be opened"
            }
        case EditDone(`imageWidthTextField`) =>
            val text = imageWidthTextField.text

            if (text.length == 0) {
                startButton.enabled = true
                imageWidthFeedbackLabel.border = Swing.LineBorder(DefaultColor)
                imageWidthFeedbackLabel.text = s"Width: ${DefaultParameters.Width} [DEFAULT]"
                filenameFeedbackLabel.text = "Filename: " + "\"" +
                        s"${DefaultParameters.Version}-" +
                        s"${DefaultParameters.Width}x" +
                        s"${if (imageHeightTextField.text == "") DefaultParameters.Height else imageHeightTextField.text}" +
                        "\"" + " [DEFAULT]"
            } else if (text.length > 9) {
                startButton.enabled = false
                imageWidthFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                imageWidthFeedbackLabel.text = s"Invalid Width! Valid range is 1 - ${DefaultParameters.MaxWidth}"
            } else {
                val value = text.toInt
                if (value > DefaultParameters.MaxWidth || value < 1) {
                    startButton.enabled = false
                    imageWidthFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                    imageWidthFeedbackLabel.text = s"Invalid Width! Valid range is 1 - ${DefaultParameters.MaxWidth}"
                } else {
                    startButton.enabled = true
                    imageWidthFeedbackLabel.border = Swing.LineBorder(ValidColor)
                    imageWidthFeedbackLabel.text = s"Width: $value"
                    filenameFeedbackLabel.text = "Filename: " + "\"" +
                            s"${DefaultParameters.Version}-" +
                            s"${value}x" +
                            s"${if (imageHeightTextField.text == "") DefaultParameters.Height else imageHeightTextField.text}" +
                            "\"" + " [DEFAULT]"
                }
            }
        case EditDone(`imageHeightTextField`) =>
            val text = imageHeightTextField.text

            if (text.length == 0) {
                startButton.enabled = true
                imageHeightFeedbackLabel.border = Swing.LineBorder(DefaultColor)
                imageHeightFeedbackLabel.text = s"Height: ${DefaultParameters.Height} [DEFAULT]"
                filenameFeedbackLabel.text = "Filename: " + "\"" +
                        s"${DefaultParameters.Version}-" +
                        s"${if (imageWidthTextField.text == "") DefaultParameters.Width else imageWidthTextField.text}x" +
                        s"${DefaultParameters.Height}" +
                        "\"" + " [DEFAULT]"
            } else if (text.length > 9) {
                startButton.enabled = false
                imageHeightFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                imageHeightFeedbackLabel.text = s"Invalid Height! Valid range is 1 - ${DefaultParameters.MaxHeight}"
            } else {
                val value = text.toInt
                if (value > DefaultParameters.MaxHeight || value < 1) {
                    startButton.enabled = false
                    imageHeightFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                    imageHeightFeedbackLabel.text = s"Invalid Height! Valid range is 1 - ${DefaultParameters.MaxHeight}"
                } else {
                    startButton.enabled = true
                    imageHeightFeedbackLabel.border = Swing.LineBorder(ValidColor)
                    imageHeightFeedbackLabel.text = s"Height: $value"
                    filenameFeedbackLabel.text = "Filename: " + "\"" +
                            s"${DefaultParameters.Version}-" +
                            s"${if (imageWidthTextField.text == "") DefaultParameters.Width else imageWidthTextField.text}x" +
                            s"$value" +
                            "\"" + " [DEFAULT]"
                }
            }
        case EditDone(`filenameTextField`) =>
            if (filenameTextField.text == "") {
                startButton.enabled = true
                filenameFeedbackLabel.border = Swing.LineBorder(DefaultColor)
                filenameFeedbackLabel.text = "Filename: " + "\"" +
                        s"${DefaultParameters.Version}-" +
                        s"${if (imageWidthTextField.text == "") DefaultParameters.Width else imageWidthTextField.text}x" +
                        s"${if (imageHeightTextField.text == "") DefaultParameters.Height else imageHeightTextField.text}" +
                        "\"" + " [DEFAULT]"
            } else if (filenameTextField.text.contains("/") || filenameTextField.text.contains("\\")) {
                startButton.enabled = false
                filenameFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                filenameFeedbackLabel.text = "Filename cannot contain slashes ('/' or '\\')"
            } else if (filenameTextField.text.contains(".")) {
                startButton.enabled = true
                filenameFeedbackLabel.border = Swing.LineBorder(WarningColor)
                filenameFeedbackLabel.text = "Filename: " + "\"" + filenameTextField.text + "\"" + " (WARN: use of '.' may have unintended consequences)"
            } else {
                startButton.enabled = true
                filenameFeedbackLabel.border = Swing.LineBorder(ValidColor)
                filenameFeedbackLabel.text = "Filename: " + "\"" + filenameTextField.text + "\""
            }
        case ButtonClicked(`filepathFileChooserButton`) =>
            val result = filepathFileChooser.showOpenDialog(null)
            if (result == FileChooser.Result.Approve) {
                filepathFeedbackLabel.border = Swing.LineBorder(ValidColor)
                filepathFeedbackLabel.text = "Output Folder: "+ "\"" + s"${filepathFileChooser.selectedFile.getPath}" + "\""
            }
        case EditDone(`imageCountTextField`) =>
            val text = imageCountTextField.text

            if (text.length == 0) {
                startButton.enabled = true
                imageCountFeedbackLabel.border = Swing.LineBorder(DefaultColor)
                imageCountFeedbackLabel.text = s"Number of Images: ${DefaultParameters.ImageCount} [DEFAULT]"
            } else if (text.length > 4) {
                startButton.enabled = false
                imageCountFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                imageCountFeedbackLabel.text = s"Invalid Number of Images! Valid range is 1 - ${DefaultParameters.MaxImageCount}"
            } else {
                val value = text.toInt
                if (value > DefaultParameters.MaxImageCount || value < 1) {
                    startButton.enabled = false
                    imageCountFeedbackLabel.border = Swing.LineBorder(InvalidColor)
                    imageCountFeedbackLabel.text = s"Invalid Number of Images! Valid range is 1 - ${DefaultParameters.MaxImageCount}"
                } else {
                    startButton.enabled = true
                    imageCountFeedbackLabel.border = Swing.LineBorder(ValidColor)
                    imageCountFeedbackLabel.text = s"Number of Images: $value"
                }
            }
    }

    /* ----- Helper Methods ----- */

    def setRunning(r: Boolean): Unit = {
        isRunning = r
    }

    def initializeParameters: Parameters = {

        val p = new Parameters

        p.Debug = DefaultParameters.Debug
        p.Version = DefaultParameters.Version

        p.ImageCount =
            if (imageHeightTextField.text == "")
                DefaultParameters.ImageCount
            else
                imageCountTextField.text.toInt

        p.OpenFile = openFileCheckBox.selected

        p.Width =
            if (imageWidthTextField.text == "")
                DefaultParameters.Width
            else
                imageWidthTextField.text.toInt

        p.Height =
            if (imageHeightTextField.text == "")
                DefaultParameters.Height
            else
                imageHeightTextField.text.toInt

        singleImageProgressBar.max = p.Width * p.Height
        multiImageProgressBar.max = p.ImageCount

        p.Filename =
            if (filenameTextField.text == "")
                s"${p.Version}-${p.Width}x${p.Height}"
            else
                filenameTextField.text

        p.Filepath = filepathFileChooser.selectedFile

        p.FileFormat = DefaultParameters.FileFormat

        p.SeedCount = DefaultParameters.SeedCount

        p.HueVariation        = DefaultParameters.HueVariation
        p.SaturationVariation = DefaultParameters.SaturationVariation
        p.BrightnessVariation = DefaultParameters.BrightnessVariation

        p.HueVariationDelta        = DefaultParameters.HueVariationDelta
        p.SaturationVariationDelta = DefaultParameters.SaturationVariationDelta
        p.BrightnessVariationDelta = DefaultParameters.BrightnessVariationDelta

        p.HueBounds        = DefaultParameters.HueBounds
        p.SaturationBounds = DefaultParameters.SaturationBounds
        p.BrightnessBounds = DefaultParameters.BrightnessBounds

        p.NorthSpreadChance = DefaultParameters.NorthSpreadChance
        p.EastSpreadChance  = DefaultParameters.EastSpreadChance
        p.SouthSpreadChance = DefaultParameters.SouthSpreadChance
        p.WestSpreadChance  = DefaultParameters.WestSpreadChance

        p.NorthSpreadChanceDelta  = DefaultParameters.NorthSpreadChanceDelta
        p.EastSpreadChanceDelta   = DefaultParameters.EastSpreadChanceDelta
        p.SouthSpreadChanceDelta  = DefaultParameters.SouthSpreadChanceDelta
        p.WestSpreadChanceDelta   = DefaultParameters.WestSpreadChanceDelta

        p
    }

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

}
