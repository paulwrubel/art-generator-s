package me.paul.artgenerators

object Parameters {

    var Debug: Boolean = true
    var Version: String = "v0.4g-alpha"

    var ImageCount: Int = 1
    var OpenFile: Boolean = false

    // HD                 : 1280  x 720
    // Full HD            : 1920  × 1080
    // True 4K            : 4096  × 2160
    // True 8K            : 8192  × 4320
    // Max Possible (32K) : 30720 x 17280
    var Width: Int  = 1920
    var Height: Int = 1080

    var Filename: String   = f"$Version-${Width}x$Height"
    var Filepath: String   = f"./out/images/$Version/${Width}x$Height/"
    var FileFormat: String = "png"

    var SeedCount: Int = 1

    var HueVariation: Double        = 1
    var SaturationVariation: Double = 0.02
    var BrightnessVariation: Double = 0.02

    var HueVariationDelta: Double        = 0
    var SaturationVariationDelta: Double = 0
    var BrightnessVariationDelta: Double = 0

    var HueBounds: (Double, Double)        = (60, 30)
    var SaturationBounds: (Double, Double) = (0.5, 1)
    var BrightnessBounds: (Double, Double) = (0.5, 1)

    var NorthSpreadChance: Double = 0.0
    var EastSpreadChance: Double  = 0.5
    var SouthSpreadChance: Double = 0.0
    var WestSpreadChance: Double  = 0.5

    // unused for now
    var NorthSpreadChanceDelta: Double = 0.0005
    var EastSpreadChanceDelta: Double  = 0.0005
    var SouthSpreadChanceDelta: Double = 0.0005
    var WestSpreadChanceDelta: Double  = 0.0005

}
