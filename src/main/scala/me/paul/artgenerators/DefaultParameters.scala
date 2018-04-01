package me.paul.artgenerators

import java.io.File

object DefaultParameters {

    val Debug: Boolean = true
    val Version: String = "v0.6-beta"

    val ImageCount: Int = 1
    val MaxImageCount: Int = 1000
    val OpenFile: Boolean = true

    // HD                 : 1280  x 720
    // Full HD            : 1920  × 1080
    // True 4K            : 4096  × 2160
    // True 8K            : 8192  × 4320
    // Max Possible (32K) : 30720 x 17280
    val Width: Int  = 1920
    val Height: Int = 1080

    val MaxWidth: Int  = 30720
    val MaxHeight: Int = 17280

    val FileFormat: String = "png"

    val SeedCount: Int = 1

    val HueVariation: Double        = 3
    val SaturationVariation: Double = 2
    val BrightnessVariation: Double = 2

    // unused for now
    val HueVariationDelta: Double        = 0
    val SaturationVariationDelta: Double = 0
    val BrightnessVariationDelta: Double = 0

    val HueBounds: (Double, Double)        = (0, 360)
    val SaturationBounds: (Double, Double) = (50, 100)
    val BrightnessBounds: (Double, Double) = (50, 100)

    val NorthSpreadChance: Double = 0
    val EastSpreadChance: Double  = 50
    val SouthSpreadChance: Double = 0
    val WestSpreadChance: Double  = 50

    val NorthSpreadChanceDelta: Double = 20
    val EastSpreadChanceDelta: Double  = 20
    val SouthSpreadChanceDelta: Double = 20
    val WestSpreadChanceDelta: Double  = 20

}
