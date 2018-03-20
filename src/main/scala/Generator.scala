import java.awt.Desktop
import java.io.IOException
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO

import scala.collection.mutable
import scala.util.Random

object Generator {

    private val Width = 500
    private val Height = 500

    private val Filename = "alpha"
    private val Filepath = "./data/"
    private val Filetype = "png"

    private val HueVariation = 3.0
    private val SaturationVariation = 0.02
    private val BrightnessVariation = 0.02

    def main(args: Array[String]): Unit = {

        println("Starting art generation...")

        println("Getting image object...")
        val image = getImage

        println("Getting pixel map...")

        println("Assigning colors to pixels...")
        val pixels = getPixels

        println("Putting colors on art")
        writeColors(image, pixels)

        println("printing object to file...")

        println("Art generation completed!")

    }

    private def getPixels: mutable.Map[Pixel, PixelData] = {

        val empty: mutable.Map[Pixel, PixelData] = mutable.Map(
                (for (
                    x <- 0 until Width;
                    y <- 0 until Height
                ) yield Pixel(x, y) -> PixelData()): _*)
        val filled = mutable.Map[Pixel, PixelData]()
        val progress = mutable.Map[Pixel, PixelData]()

        val seed = Pixel(Random.nextInt(Width), Random.nextInt(Height))
        val seedData = PixelData(Some(Color.hsb(Random.nextDouble() * 360, 0.8, 0.8)))

        // create seed
        filled(seed) = seedData
        empty -= seed

        // set neighboring pixels parents as seed and mark as changing
        val neighbors = getNeighbors(mutable.Map(seed -> seedData), empty)
        progress ++= neighbors
        empty --= neighbors.keys

        var count = 0

        // while image is not filled
        while (empty.nonEmpty) {

            val time1 = System.nanoTime()

            // set their color based on parent
            progress.transform((p,d) => {
                PixelData(Some(getVariedColor(filled(d.parent.get).color.get)))
            })

            // get their neighbors
            val neighbors = getNeighbors(progress, empty)

            val time2 = System.nanoTime()

            // move pixels to completed
            filled ++= progress
            progress.clear()

            // move neighbors to progress
            progress ++= neighbors
            empty --= neighbors.keys

            if (count % 5 == 0) {
                println("Time this round: " + (time2 - time1) + ", Amount Done: " + empty.size + " / " + Width * Height)
            }
            count += 1

        }

        filled
    }

    def writeColors(image: WritableImage, pixels: mutable.Map[Pixel, PixelData]): Unit = {

        // write colors from map into image file
        pixels.foreach(p => {
            image.getPixelWriter.setColor(p._1.x, p._1.y, p._2.color.get)
        })

        try {
            var count = 1
            var imageFile = new java.io.File(Filepath + Filename + "_" + count + "." + Filetype)
            while ( imageFile.exists ) {
                count += 1
                imageFile = new java.io.File(Filepath + Filename + "_" + count + "." + Filetype)
            }
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), Filetype, imageFile)
            Desktop.getDesktop.open(imageFile)
        } catch {
            case ioe: IOException =>
                ioe.printStackTrace()
        }

    }

    def getVariedColor(color: Color): Color = {

        def getNewValue(value: Double, variation: Double, lb: Double, ub: Double): Double = {

            // get variation from -v to v
            def getVariation(v: Double) = ( Random.nextDouble() * (-2 * v) ) + v

            // get a new possible hue
            val possibleValue = value + getVariation(variation)

            // return true new value in valid range 0 to 360
            if (possibleValue > ub) {
                possibleValue - ub
            } else if (possibleValue < lb) {
                possibleValue + ub
            } else {
                possibleValue
            }

        }

        // get new values
        val newHue = getNewValue(color.getHue, HueVariation, 0, 360)
        val newSat = getNewValue(color.getSaturation, SaturationVariation, 0, 1)
        val newBright = getNewValue(color.getBrightness, BrightnessVariation, 0, 1)

        Color.hsb(newHue, newSat, newBright)
    }

    def getNeighbors(parents: mutable.Map[Pixel, PixelData], empty: mutable.Map[Pixel, PixelData]
                    ): mutable.Map[Pixel, PixelData] = {

        val neighbors = mutable.Map[Pixel, PixelData]()

        parents.foreach(
            p => {
                neighbors += Pixel(p._1.x + 1, p._1.y    ) -> PixelData(parent = Some(p._1))
                neighbors += Pixel(p._1.x    , p._1.y + 1) -> PixelData(parent = Some(p._1))
                neighbors += Pixel(p._1.x - 1, p._1.y    ) -> PixelData(parent = Some(p._1))
                neighbors += Pixel(p._1.x    , p._1.y - 1) -> PixelData(parent = Some(p._1))
            }
        )

        neighbors.filter(p => empty.contains(p._1))
    }

    def getImage: WritableImage = new WritableImage(Width, Height)
}

case class Pixel(x: Int, y: Int)
case class PixelData(color: Option[Color] = None, parent: Option[Pixel] = None)