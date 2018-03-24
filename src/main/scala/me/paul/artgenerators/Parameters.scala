package me.paul.artgenerators

object Parameters {

    val Debug = true
    val Version = "v0.2-alpha"

    val ImageCount = 5
    val OpenFile = true

    // HD       : 1280 x 720
    // Full HD  : 1920 × 1080
    // True 4K  : 4096 × 2160
    // True 8K  : 8192 × 4320
    val Width = 1920
    val Height = 1080

    val Filename = f"$Version-${Width}x$Height"
    val Filepath = f"./out/images/$Version/${Width}x$Height/"
    val FileFormat = "png"

    val HueVariation = 1.0
    val SaturationVariation = 0.02
    val BrightnessVariation = 0.02

    val HueBounds: (Double, Double) = (180.0, 360.0)
    val SaturationBounds: (Double, Double) = (0.85, 1)
    val BrightnessBounds: (Double, Double) = (0.85, 1)

    val NorthSpreadChance = 0.5
    val EastSpreadChance = 0.5
    val SouthSpreadChance = 0.5
    val WestSpreadChance = 0.5

    // unused for now
    val NorthSpreadChanceDelta = 0.001
    val EastSpreadChanceDelta = 0.001
    val SouthSpreadChanceDelta = 0.001
    val WestSpreadChanceDelta = 0.001

}
