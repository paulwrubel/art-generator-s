package me.paul.artgenerators

object Parameters {

    val Debug: Boolean = true
    val Version: String = "v0.3-alpha"

    val ImageCount: Int = 1
    val OpenFile: Boolean = true

    // HD                 : 1280  x 720
    // Full HD            : 1920  × 1080
    // True 4K            : 4096  × 2160
    // True 8K            : 8192  × 4320
    // Max Possible (32K) : 30720 x 17280
    val Width: Int  = 1920
    val Height: Int = 1080

    val Filename: String   = f"$Version-${Width}x$Height"
    val Filepath: String   = f"./out/images/$Version/${Width}x$Height/"
    val FileFormat: String = "png"

    val SeedCount: Int = 1

    val HueVariation: Double        = 5
    val SaturationVariation: Double = 0.02
    val BrightnessVariation: Double = 0.02

    val HueBounds: (Double, Double)        = (90, 30)
    val SaturationBounds: (Double, Double) = (0.5, 1)
    val BrightnessBounds: (Double, Double) = (0.5, 1)

    val NorthSpreadChance: Double = 0.25
    val EastSpreadChance: Double  = 0.75
    val SouthSpreadChance: Double = 0.25
    val WestSpreadChance: Double  = 0.75

    // unused for now
    val NorthSpreadChanceDelta: Double = -0.01
    val EastSpreadChanceDelta: Double  = 0.01
    val SouthSpreadChanceDelta: Double = -0.01
    val WestSpreadChanceDelta: Double  = 0.01

}
