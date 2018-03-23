package me.paul.artgenerators

object Parameters {

    val Debug = true

    val Width = 12000
    val Height = 6000

    val Filename = "alpha"
    val Filepath = "./out/images/alpha/extralarge/"
    val FileFormat = "png"

    val HueVariation = 3.0
    val SaturationVariation = 0.02
    val BrightnessVariation = 0.02

    val HueBounds: (Double, Double) = (0.0, 360.0)
    val SaturationBounds: (Double, Double) = (0.75, 1)
    val BrightnessBounds: (Double, Double) = (0.25, 1)

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
