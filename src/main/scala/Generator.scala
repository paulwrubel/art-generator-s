import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

import scala.collection.mutable
import scala.util.Random

object Generator {

    private val Width = 500
    private val Height = 500

    private val HueVariation = 3.0
    private val SaturationVariation = 0.02
    private val BrightnessVariation = 0.02

    def main(args: Array[String]): Unit = {

        println("Starting art generation...")

        println("Getting image object...")
        val image = getImage

        println("Getting pixel map...")
        val pixelData = getPixelData(image)

        println("Assigning colors to pixels...")
        fillImage(image, pixelData)

        println("Putting colors on art")

        println("printing object to file...")

        println("Art generation completed!")

    }

    private def getImage: WritableImage = new WritableImage(Width, Height)

    private def getPixelData(image: WritableImage): mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)] = {

        val pixelData = mutable.HashMap[(Int, Int), ((Int, Int), PixelStatus, Color)]()

        for (
            x <- 0 until Width;
            y <- 0 until Height
        ) {
            pixelData += ((x, y) -> (null, PixelStatus.empty, null))
        }

        pixelData
    }

    private def fillImage(image: WritableImage,
                          pixelData: mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)]
                         ): Unit = {

        val seedLocation = (Random.nextInt(Width), Random.nextInt(Height))

        // create seed
        pixelData(seedLocation) = (
                null,
                PixelStatus.complete,
                Color.hsb(Random.nextDouble() * 360, 0.8, 0.8)
        )

        // set neighboring pixels parents as seed and mark as changing
        val n = getNeighbors(pixelData, mutable.Map(seedLocation -> pixelData(seedLocation)))
        pixelData ++= n

        var test = 0

        // while image is not filled
        while (pixelData.exists(_._2._2 == PixelStatus.empty)) {

            //println(pixelData.count(_._2._2 == PixelStatus.empty))


            // get all current seeds
            val seeds = pixelData.filter(_._2._2 == PixelStatus.toChange)



            val time1 = System.nanoTime()

            // set their color based on parent and mark them as completed
            seeds.mapValues((v) =>
                (v._1, PixelStatus.complete, getVariedColor( pixelData( v._1 )._3) )
            )


            val time2 = System.nanoTime()

            // get their neighbors
            val neighbors = getNeighbors(pixelData, seeds)

            // mark their neighbors as changing and set their parent to seeds
            pixelData ++= neighbors

            test += 1

            if (test % 5 == 0) println("Time this round: " + (time2 - time1)
                    + ", Amount Done: " + pixelData.count(_._2._2 == PixelStatus.empty) + " / " + pixelData.size)

        }

    }

    private def getNeighbors(pixelData: mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)],
                             parents: mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)]
                            ): mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)] = {

        val neighbors = mutable.Map[(Int, Int), ((Int, Int), PixelStatus, Color)]()

        parents.foreach(
            (p) => {
                neighbors((p._1._1 + 1, p._1._2    )) = (p._1, PixelStatus.toChange, null)
                neighbors((p._1._1    , p._1._2 + 1)) = (p._1, PixelStatus.toChange, null)
                neighbors((p._1._1 - 1, p._1._2    )) = (p._1, PixelStatus.toChange, null)
                neighbors((p._1._1    , p._1._2 - 1)) = (p._1, PixelStatus.toChange, null)
            }
        )

        neighbors.filter((p) => pixelData.contains(p._1) && pixelData(p._1)._2 == PixelStatus.empty)
    }

    private def getVariedColor(color: Color): Color = {

        // get new values
        val newHue = getNewHue(color.getHue)
        val newSat = getNewSaturation(color.getSaturation)
        val newBright = getNewBrightness(color.getBrightness)

        Color.hsb(newHue, newSat, newBright)
    }

    private def getNewHue(hue: Double): Double = {

        // get a new possible hue
        val possibleHue = hue + getVariation(HueVariation)

        // return true new value in valid range 0 to 360
        if (possibleHue > 360) {
            possibleHue - 360
        } else if (possibleHue < 360) {
            possibleHue + 360
        } else {
            possibleHue
        }

    }

    private def getNewSaturation(sat: Double): Double = {

        // get a new possible hue
        val possibleSat = sat + getVariation(SaturationVariation)

        // return true new value in valid range 0 to 360
        if (possibleSat > 1) {
            possibleSat - 1
        } else if (possibleSat < 1) {
            possibleSat + 1
        } else {
            possibleSat
        }

    }

    private def getNewBrightness(bright: Double): Double = {

        // get a new possible hue
        val possibleBright = bright + getVariation(BrightnessVariation)

        // return true new value in valid range 0 to 360
        if (possibleBright > 1) {
            possibleBright - 1
        } else if (possibleBright < 1) {
            possibleBright + 1
        } else {
            possibleBright
        }

    }

    // get variation from -v to v
    private def getVariation(v: Double) = ( Random.nextDouble() * (-2 * v) ) + v

}