package me.paul.artgenerators

import java.awt.Desktop
import java.io.{File, IOException}
import java.util

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import javax.swing.SwingWorker

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.swing.{ProgressBar, TextArea}
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

class Generator(params: Parameters, output: TextArea, progressBar: ProgressBar) extends SwingWorker[Unit, Int] {

    // TODO Comment explanations

    override def doInBackground(): Unit = {
        startGeneration()
    }

    override def process(chunks: util.List[Int]): Unit = {
        val scalaChunks = chunks.asScala

        progressBar.value = scalaChunks.last
        if (progressBar.label != "...PRINTING...")
            progressBar.label = f"${100 * progressBar.value.asInstanceOf[Double] / progressBar.max}%6.2f %%"

    }

    override def done(): Unit = {
        progressBar.value = progressBar.max
        progressBar.label = f"${100.asInstanceOf[Double]}%6.2f %%"
        progressBar.indeterminate = false
    }

    def startGeneration(): Unit = {
        output.text += "Starting program...\n"

        (1 to params.ImageCount).foreach(generateArt)

        output.text += "\n"
        output.text += "All images successfully printed!\n"
        output.text += "\n"

    }

    def generateArt(imageNum: Int): Unit = {

        output.text += f"[Image #$imageNum%4d] Starting art generation...\n"

        output.text += f"[Image #$imageNum%4d] Getting image object: [${params.Width} x ${params.Height}]...\n"
        val imageTimeStart = System.nanoTime
        val image = getImage
        val imageTimeEnd = System.nanoTime

        output.text += f"[Image #$imageNum%4d] Beginning generation...\n"
        val fillTimeStart = System.nanoTime
        fillImage(image, imageNum)
        val fillTimeEnd = System.nanoTime

        progressBar.indeterminate = true
        progressBar.label = "...PRINTING..."

        output.text += f"[Image #$imageNum%4d] printing object to file...\n"
        val fileTimeStart = System.nanoTime
        val file = getFile
        writeImageToFile(image, file)
        val fileTimeEnd = System.nanoTime

        output.text += f"[Image #$imageNum%4d] Art generation completed!\n"
        output.text += "\n"

        if (params.OpenFile) openFile(file)

        if (params.Debug) {
            val imageTime = imageTimeEnd - imageTimeStart
            val fillTime = fillTimeEnd - fillTimeStart
            val fileTime = fileTimeEnd - fileTimeStart

            val imagePercentage = imageTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100
            val fillPercentage = fillTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100
            val filePercentage = fileTime.asInstanceOf[Double] / (imageTime + fillTime + fileTime) * 100

            output.text += f"[Image #$imageNum%4d] --- START DEBUG INFO ---\n"
            output.text += f"[Image #$imageNum%4d]\n"
            output.text += f"[Image #$imageNum%4d] Image Size: ${params.Width}%,d x ${params.Height}%,d\n"
            output.text += f"[Image #$imageNum%4d] Total pixel count: ${params.Width * params.Height}%,d\n"
            output.text += f"[Image #$imageNum%4d]\n"
            output.text += f"[Image #$imageNum%4d] Time to create WritableImage:    $imageTime%,16dns (${imageTime / 1000000f}%,11.2fms) - [$imagePercentage%6.2f%% of total time]\n"
            output.text += f"[Image #$imageNum%4d] Time to assign colors to pixels: $fillTime%,16dns (${fillTime / 1000000f}%,11.2fms) - [$fillPercentage%6.2f%% of total time]\n"
            output.text += f"[Image #$imageNum%4d] Time to print Image to File:     $fileTime%,16dns (${fileTime / 1000000f}%,11.2fms) - [$filePercentage%6.2f%% of total time]\n"
            output.text += f"[Image #$imageNum%4d]\n"
            output.text += f"[Image #$imageNum%4d] --- END DEBUG INFO ---\n"
        }

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

        val progress: mutable.Set[(Int, Int)] = mutable.Set()
        output.text += f"[Image #$imageNum%4d]     ...Setting seeds...\n"

        (1 to params.SeedCount).foreach(_ => {

            val condition = (p: (Int, Int)) => read.getColor(p._1, p._2).isOpaque
            val seed = doWhileYield[(Int, Int)](condition) {
                (Random.nextInt(params.Width), Random.nextInt(params.Height))
            }
            val seedColor =
                if (params.HueBounds._2 < params.HueBounds._1) {
                    Color.hsb(
                        randomBetween(params.HueBounds._1)(360.0 + params.HueBounds._2) % 360,
                        randomBounds(params.SaturationBounds) / 100,
                        randomBounds(params.BrightnessBounds) / 100
                    )
                } else {
                    Color.hsb(
                        randomBounds(params.HueBounds),
                        randomBounds(params.SaturationBounds) / 100,
                        randomBounds(params.BrightnessBounds) / 100
                    )
                }

            // create seed
            write.setColor(seed._1, seed._2, seedColor)

            progress += seed

        })

        var round = 0
        var pixelCount = params.SeedCount

        // while image is not filled
        output.text += f"[Image #$imageNum%4d]     ...Starting rounds of generations...\n"
        whileLoop(progress.nonEmpty) {

            val time1 = System.nanoTime

            // set their color based on parent
            progress.foreach(
                p => {
                    var completed = true

                    def getSpreadChance(chance: Double, delta: Double) = {
                        if (delta >= 0) {
                            val x = (chance + (delta * round)) % 2.0
                            if (x >= 1.0) {
                                1 - ((chance + (round * delta)) % 1.0)
                            } else {
                                (chance + (round * delta)) % 1.0
                            }
                        } else {
                            val x = (((chance + (delta * round)) % 2.0) + 2.0) % 2.0
                            //output.text += "result: " + x)
                            if (x < 1.0) {
                                (((chance + (delta * round)) % 1.0) + 1.0) % 1.0
                            } else {
                                1 - ((((chance + (delta * round)) % 1.0) + 1.0) % 1.0)
                            }
                        }
                    }

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

                    if (p._2 != 0)                     handlePixel((p._1    , p._2 - 1), getSpreadChance(params.NorthSpreadChance / 100, params.NorthSpreadChanceDelta / 10000))
                    if (p._1 != params.Width - 1)  handlePixel((p._1 + 1, p._2    ), getSpreadChance(params.EastSpreadChance  / 100, params.EastSpreadChanceDelta  / 10000))
                    if (p._2 != params.Height - 1) handlePixel((p._1    , p._2 + 1), getSpreadChance(params.SouthSpreadChance / 100, params.SouthSpreadChanceDelta / 10000))
                    if (p._1 != 0)                     handlePixel((p._1 - 1, p._2    ), getSpreadChance(params.WestSpreadChance  / 100, params.WestSpreadChanceDelta  / 10000))

                    if (completed) {
                        progress -= p
                    }
                }
            )

            val time2 = System.nanoTime
            if (params.Debug && round % 10 == 0) {

                val progress = 100 * pixelCount.asInstanceOf[Double] / (params.Width * params.Height)
                publish(pixelCount)

                output.text += f"[Image #$imageNum%4d]     Round $round%6d: " +
                        f"Time: ${time2 - time1}%,15dns, " +
                        f"Pixels Completed: $pixelCount%,13d / ${params.Width * params.Height}%,13d " +
                        f"[${100 * pixelCount.asInstanceOf[Double] / (params.Width * params.Height)}%6.2f%%]\n"
            }
            round += 1

        }
    }

    def getFile: File = {
        val dir = params.Filepath
        println(dir)
        if ( !dir.exists && !dir.mkdirs() ) {
            throw new IOException("[ERROR]: COULD NOT CREATE NECESSARY DIRECTORIES")
        }

        var count = 0
        val exists = (f: File) => f.exists()
        doWhileYield[File](exists) {
            count += 1
            new File(params.Filepath.getPath + "/" + params.Filename + "_" + count + "." + params.FileFormat)
        }
    }

    /** Writes a [[WritableImage]] to a [[File]]
      *
      * @param image the [[WritableImage]] to read write to a [[File]]
      * @param file the [[File]] to write the [[WritableImage]] to
      */

    def writeImageToFile(image: WritableImage, file: File): Unit = {

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), params.FileFormat, file)
        } catch {
            case ioe: IOException =>
                ioe.printStackTrace()
        }

    }

    def openFile(file: File): Unit = Desktop.getDesktop.open(file)


    // TODO: add functionality for HueVariationDelta, SatVariationDelta, BrightVariationDelta
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
                if (circular && bounds._2 < bounds._1) {
                    (d >= bounds._1 && d <= 360.0) || (d >= 0.0 && d <= bounds._2)
                } else {
                    d >= bounds._1 && d <= bounds._2
                }
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
        val newHue = getNewValue(color.getHue, params.HueVariation, params.HueBounds, circular = true)
        val newSat = getNewValue(color.getSaturation * 100, params.SaturationVariation, params.SaturationBounds, circular = false) / 100
        val newBright = getNewValue(color.getBrightness * 100, params.BrightnessVariation, params.BrightnessBounds, circular = false) / 100
        Color.hsb(newHue, newSat, newBright)
    }

    def getImage: WritableImage = new WritableImage(params.Width, params.Height)

    @tailrec
    final def whileLoop(condition: => Boolean)(func: => Unit): Unit = {
        if (condition) {
            func
            whileLoop(condition)(func)
        }
    }

    @tailrec
    final def whileYield[A](condition: => Boolean)(func: => A): A = {
        if (!condition) {
            func
        } else {
            whileYield(condition)(func)
        }
    }

    @tailrec
    final def doWhile[A](condition: A => Boolean)(func: => A): Unit = {
        val newA = func
        if (condition(newA)) {
            doWhile(condition)(func)
        }
    }

    @tailrec
    final def doWhileYield[A](condition: A => Boolean)(func: => A): A = {
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