package me.paul.artgenerators

import java.awt.Desktop
import java.io.IOException
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random

object Generator {

    def main(args: Array[String]): Unit = {

        println("Starting art generation...")

        println("Getting image object...")
        val imageTimeStart = System.nanoTime()
        val image = getImage
        val imageTimeEnd = System.nanoTime()

        println("Beginning generation...")
        val fillTimeStart = System.nanoTime()
        val pixels = getPixels
        val fillTimeEnd = System.nanoTime()

        println("Putting colors on art")
        val putColorsTimeStart = System.nanoTime()
        putColors(image, pixels)
        val putColorsTimeEnd = System.nanoTime()

        println("printing object to file...")
        val fileTimeStart = System.nanoTime()
        writeColors(image)
        val fileTimeEnd = System.nanoTime()

        println("Art generation completed!")
        println()

        if (Parameters.Debug) {
            val imageTime = imageTimeEnd - imageTimeStart
            val fillTime = fillTimeEnd - fillTimeStart
            val putColorsTime = putColorsTimeEnd - putColorsTimeStart
            val fileTime = fileTimeEnd - fileTimeStart

            val imagePerc = imageTime.asInstanceOf[Double] / (imageTime + fillTime + putColorsTime + fileTime) * 100
            val fillPerc = fillTime.asInstanceOf[Double] / (imageTime + fillTime + putColorsTime + fileTime) * 100
            val putColorsPerc = putColorsTime.asInstanceOf[Double] / (imageTime + fillTime + putColorsTime + fileTime) * 100
            val filePerc = fileTime.asInstanceOf[Double] / (imageTime + fillTime + putColorsTime + fileTime) * 100


            println("--- DEBUG INFO: ---")
            println()
            println(f"Image Size: ${Parameters.Width} x ${Parameters.Height}")
            println(f"Total pixel count: ${Parameters.Width * Parameters.Height}")
            println()
            println(f"Time to create WritableImage:    $imageTime%,16dns (${imageTime / 1000000f}%,11.2fms) - [$imagePerc%6.2f%% of total time]")
            println(f"Time to assign colors to pixels: $fillTime%,16dns (${fillTime / 1000000f}%,11.2fms) - [$fillPerc%6.2f%% of total time]")
            println(f"Time to put colors on image:     $putColorsTime%,16dns (${putColorsTime / 1000000f}%,11.2fms) - [$putColorsPerc%6.2f%% of total time]")
            println(f"Time to print Image to File:     $fileTime%,16dns (${fileTime / 1000000f}%,11.2fms) - [$filePerc%6.2f%% of total time]")
            println()
        }

    }

    private def getPixels: mutable.Map[Pixel, PixelData] = {

        println("    ...Getting pixel map...")
        val empty: mutable.Map[Pixel, PixelData] = mutable.Map(
                (for (
                    x <- 0 until Parameters.Width;
                    y <- 0 until Parameters.Height
                ) yield Pixel(x, y) -> PixelData()): _*)
        val filled = mutable.Map[Pixel, PixelData]()
        val progress = mutable.Map[Pixel, PixelData]()

        println("    ...Setting seeds...")
        val seed = Pixel(Random.nextInt(Parameters.Width), Random.nextInt(Parameters.Height))
        val seedData = PixelData(Some(Color.hsb(randomBounds(Parameters.HueBounds), randomBounds(Parameters.SaturationBounds), randomBounds(Parameters.BrightnessBounds))))

        // create seed
        filled(seed) = seedData
        empty -= seed

        // set neighboring pixels parents as seed and mark as changing
        val neighbors = getNeighbors(mutable.Map(seed -> seedData), empty)
        progress ++= neighbors
        empty --= neighbors.keys

        var count = 0

        // while image is not filled
        println("    ...Starting rounds of generations...")
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
                println("Time this round: " + (time2 - time1) + ", Amount Done: " + empty.size + " / " + Parameters.Width * Parameters.Height)
            }
            count += 1

        }

        filled
    }

    def putColors(image: WritableImage, pixels: mutable.Map[Pixel, PixelData]): Unit = {

        // write colors from map into image file
        pixels.foreach(p => {
            image.getPixelWriter.setColor(p._1.x, p._1.y, p._2.color.get)
        })

    }

    def writeColors(image: WritableImage): Unit = {

        try {
            var count = 0
            val exists = (f: java.io.File) => f.exists()
            val imageFile = doWhileYield[java.io.File]( exists ) {
                count += 1
                new java.io.File(Parameters.Filepath + Parameters.Filename + "_" + count + "." + Parameters.FileFormat)
            }
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), Parameters.FileFormat, imageFile)
            Desktop.getDesktop.open(imageFile)
        } catch {
            case ioe: IOException =>
                ioe.printStackTrace()
        }

    }

    def getVariedColor(color: Color): Color = {

        def getNewValue(value: Double, variation: Double, bounds: (Double, Double), circular: Boolean): Double = {

            def normalize(oldValue: Double): Double = {
                if (oldValue < 0) {
                    oldValue + 360
                } else {
                    oldValue % 360
                }
            }

            val condition = (d: Double) => {
                d >= bounds._1 && d <= bounds._2
            }

            doUntilYield[Double](condition) {
                if (circular) {
                    normalize(value + randomVariation(variation))
                } else {
                    value + randomVariation(variation)
                }
            }
        }

        // get new values
        val newHue = getNewValue(color.getHue, Parameters.HueVariation, Parameters.HueBounds, circular = true)
        val newSat = getNewValue(color.getSaturation, Parameters.SaturationVariation, Parameters.SaturationBounds, circular = false)
        val newBright = getNewValue(color.getBrightness, Parameters.BrightnessVariation, Parameters.BrightnessBounds, circular = false)
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

    def getImage: WritableImage = new WritableImage(Parameters.Width, Parameters.Height)

    @tailrec
    def whileLoop(condition: => Boolean)(func: => Unit): Unit = {
        if (condition) {
            func
            whileLoop(condition)(func)
        }
    }

    @tailrec
    def whileYield[A](condition: => Boolean)(func: => A): A = {
        if (!condition) {
            func
        } else {
            whileYield(condition)(func)
        }
    }

    @tailrec
    def doWhile[A](condition: A => Boolean)(func: => A): Unit = {
        val newA = func
        if (condition(newA)) {
            doWhile(condition)(func)
        }
    }

    @tailrec
    def doWhileYield[A](condition: A => Boolean)(func: => A): A = {
        val newA = func
        if (!condition(newA)) {
            newA
        } else {
            doWhileYield(condition)(func)
        }
    }

    def untilLoop(condition: => Boolean)(func: => Unit): Unit = whileLoop(!condition)(func)

    def untilYield[A](condition: => Boolean)(func: => A): A = whileYield(!condition)(func)

    def doUntil[A](condition: A => Boolean)(func: => A): Unit = doWhileYield[A]((a: A) => !condition(a))(func)

    def doUntilYield[A](condition: A => Boolean)(func: => A): A = doWhileYield[A]((a: A) => !condition(a))(func)

    def randomBetween(lb: Double)(ub: Double): Double = {
        Random.nextDouble() * (ub - lb) + lb
    }

    def randomBounds(bounds: (Double, Double)): Double = randomBetween(bounds._1)(bounds._2)

    def randomUpTo(ub: Double): Double = randomBetween(0.0)(ub)

    def randomVariation(v: Double): Double = randomBetween(-v)(v)
}

case class Pixel(x: Int, y: Int)
case class PixelData(color: Option[Color] = None, parent: Option[Pixel] = None)