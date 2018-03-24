package me.paul.artgenerators

import java.awt.Desktop
import java.io.{File, IOException}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random

// TODO more detailed scaladoc
/** Main engine for art generation
  *
  * Creates generated artwork using scala, java, and [[javafx]] libraries.
  *
  * Creation is done procedurally, starting with one or more seeds.
  * Colors are varied from the seed until the image is completed
  *
  * Implemented using [[WritableImage]], [[Color]] for data storage
  *
  * data is also stored is several instances of [[mutable.Map]]
  *
  */

object Generator {

    // TODO Comment explanations

    /** entry point for Generator object
      *
      * @param args the arguments passed to this object
      */

    def main(args: Array[String]): Unit = {

        println()
        println("Starting program...")

        (1 to Parameters.ImageCount).foreach((imageNum) => {

            println(f"[Image #$imageNum%4d] Starting art generation...")

            println(f"[Image #$imageNum%4d] Getting image object...")
            val imageTimeStart = System.nanoTime
            val image = getImage
            val imageTimeEnd = System.nanoTime

            println(f"[Image #$imageNum%4d] Beginning generation...")
            val fillTimeStart = System.nanoTime
            fillImage(image, imageNum)
            val fillTimeEnd = System.nanoTime

            println(f"[Image #$imageNum%4d] printing object to file...")
            val fileTimeStart = System.nanoTime
            val file = getFile
            writeImageToFile(image, file)
            val fileTimeEnd = System.nanoTime

            println(f"[Image #$imageNum%4d] Art generation completed!")
            println()

            if (Parameters.OpenFile) openFile(file)

            if (Parameters.Debug) {
                val imageTime = imageTimeEnd - imageTimeStart
                val fillTime = fillTimeEnd - fillTimeStart
                val fileTime = fileTimeEnd - fileTimeStart

                val imagePercentage = imageTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100
                val fillPercentage = fillTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100
                val filePercentage = fileTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100

                println(f"[Image #$imageNum%4d] --- START DEBUG INFO ---")
                println(f"[Image #$imageNum%4d]")
                println(f"[Image #$imageNum%4d] Image Size: ${Parameters.Width} x ${Parameters.Height}")
                println(f"[Image #$imageNum%4d] Total pixel count: ${Parameters.Width * Parameters.Height}")
                println(f"[Image #$imageNum%4d]")
                println(f"[Image #$imageNum%4d] Time to create WritableImage:    $imageTime%,16dns (${imageTime / 1000000f}%,11.2fms) - [$imagePercentage%6.2f%% of total time]")
                println(f"[Image #$imageNum%4d] Time to assign colors to pixels: $fillTime%,16dns (${fillTime / 1000000f}%,11.2fms) - [$fillPercentage%6.2f%% of total time]")
                println(f"[Image #$imageNum%4d] Time to print Image to File:     $fileTime%,16dns (${fileTime / 1000000f}%,11.2fms) - [$filePercentage%6.2f%% of total time]")
                println(f"[Image #$imageNum%4d]")
                println(f"[Image #$imageNum%4d] --- END DEBUG INFO ---")
                println(f"[Image #$imageNum%4d]")
            }

        })

        println()
        println("All images successfully printed!")
        println()

    }

    /** Returns a [[mutable.Map]] containing a map from a
      *
      * It is used to attach final colors to the [[WritableImage]]
      *
      * @return a [[mutable.Map]] with final mapping from each
      */

    private def fillImage(image: WritableImage, imageNum: Int): Unit = {

        val read = image.getPixelReader
        val write = image.getPixelWriter

        println(f"[Image #$imageNum%4d]     ...Getting pixel map...")
        val progress: mutable.Set[(Int, Int)] = mutable.Set()
        println(f"[Image #$imageNum%4d]     ...Setting seeds...")
        val seed = (Random.nextInt(Parameters.Width), Random.nextInt(Parameters.Height))
        val seedColor = Color.hsb(
            randomBounds(Parameters.HueBounds),
            randomBounds(Parameters.SaturationBounds),
            randomBounds(Parameters.BrightnessBounds)
        )

        // create seed
        write.setColor(seed._1, seed._2, seedColor)

        progress += seed

        var count = 0
        var pixelCount = 1

        // while image is not filled
        println(f"[Image #$imageNum%4d]     ...Starting rounds of generations...")
        whileLoop(progress.nonEmpty) {

            val time1 = System.nanoTime

            // set their color based on parent
            progress.foreach(
                p => {
                    var completed = true

                    def handlePixel(newPixel: (Int, Int), sc: Double): Unit = {
                        if (!read.getColor(newPixel._1, newPixel._2).isOpaque) {
                            if (randomUpTo(1) < sc) {
                                write.setColor(newPixel._1, newPixel._2, getVariedColor( read.getColor(p._1, p._2) ))
                                progress += newPixel
                                pixelCount += 1
                            } else {
                                completed = false
                            }
                        }
                    }

                    if (p._2 != 0)                     handlePixel((p._1    , p._2 - 1), Parameters.NorthSpreadChance)
                    if (p._1 != Parameters.Width - 1)  handlePixel((p._1 + 1, p._2    ), Parameters.EastSpreadChance)
                    if (p._2 != Parameters.Height - 1) handlePixel((p._1    , p._2 + 1), Parameters.SouthSpreadChance)
                    if (p._1 != 0)                     handlePixel((p._1 - 1, p._2    ), Parameters.WestSpreadChance)

                    if (completed) {
                        progress -= p
                    }
                }
            )

            val time2 = System.nanoTime

            if (count % 10 == 0) {
                println(f"[Image #$imageNum%4d]     Round $count%6d: " +
                        f"Time: ${time2 - time1}%,15dns, " +
                        f"Pixels Completed: $pixelCount%,13d / ${Parameters.Width * Parameters.Height}%,13d " +
                        f"[${100 * pixelCount.asInstanceOf[Double] / (Parameters.Width * Parameters.Height)}%6.2f%%]")
            }
            count += 1

        }
    }

    def getFile: File = {
        val dir = new File(Parameters.Filepath)
        if ( !dir.exists && !dir.mkdirs() ) {
            throw new IOException("[ERROR]: COULD NOT CREATE NECESSARY DIRECTORIES")
        }

        var count = 0
        val exists = (f: File) => f.exists()
        doWhileYield[File](exists) {
            count += 1
            new File(Parameters.Filepath + Parameters.Filename + "_" + count + "." + Parameters.FileFormat)
        }
    }

    /** Writes a [[WritableImage]] to a [[File]]
      *
      * @param image the [[WritableImage]] to read write to a [[File]]
      * @param file the [[File]] to write the [[WritableImage]] to
      */

    def writeImageToFile(image: WritableImage, file: File): Unit = {

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), Parameters.FileFormat, file)
        } catch {
            case ioe: IOException =>
                ioe.printStackTrace()
        }

    }

    def openFile(file: File): Unit = Desktop.getDesktop.open(file)

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