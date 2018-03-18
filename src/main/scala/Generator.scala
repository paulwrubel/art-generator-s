import javafx.geometry.Point2D
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

import scala.util.Random

object Generator {

    val Width = 500
    val Height = 500

    def main(args: Array[String]): Unit = {

        val image = getImage

        fillImage(image)

    }

    def getImage: WritableImage = new WritableImage(Width, Height)

    def fillImage(value: WritableImage): Unit = {

        val seeds = List[Point2D](new Point2D(Random.nextInt(Width), Random.nextInt(Height)))

    }

}