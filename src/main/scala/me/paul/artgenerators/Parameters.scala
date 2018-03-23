package me.paul.artgenerators

object Parameters {

    val Debug = true

    val Width = 1600
    val Height = 800

    val Filename = "expir-test"
    val Filepath = "./data/"
    val FileFormat = "png"

    val HueVariation = 3.0
    val SaturationVariation = 0.02
    val BrightnessVariation = 0.02

    val HueBounds: (Double, Double) = (0.0, 360.0)
    val SaturationBounds: (Double, Double) = (0.8, 1.0)
    val BrightnessBounds: (Double, Double) = (0.9, 1.0)

    val NorthSpreadChance = 0.5
    val EastSpreadChance = 0.5
    val SouthSpreadChance = 0.5
    val WestSpreadChance = 0.5

}
