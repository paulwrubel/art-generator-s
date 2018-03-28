package me.paul.artgenerators

object Parameters {

    var Debug: Boolean = DefaultParameters.Debug
    var Version: String = DefaultParameters.Version

    var ImageCount: Int = DefaultParameters.ImageCount
    var OpenFile: Boolean = DefaultParameters.OpenFile

    var Width: Int  = DefaultParameters.Width
    var Height: Int = DefaultParameters.Height

    var Filename: String   = DefaultParameters.Filename
    var Filepath: String   = DefaultParameters.Filepath
    var FileFormat: String = DefaultParameters.FileFormat

    var SeedCount: Int = DefaultParameters.SeedCount

    var HueVariation: Double        = DefaultParameters.HueVariation
    var SaturationVariation: Double = DefaultParameters.SaturationVariation
    var BrightnessVariation: Double = DefaultParameters.BrightnessVariation

    var HueVariationDelta: Double        = DefaultParameters.HueVariationDelta
    var SaturationVariationDelta: Double = DefaultParameters.SaturationVariationDelta
    var BrightnessVariationDelta: Double = DefaultParameters.BrightnessVariationDelta

    var HueBounds: (Double, Double)        = DefaultParameters.HueBounds
    var SaturationBounds: (Double, Double) = DefaultParameters.SaturationBounds
    var BrightnessBounds: (Double, Double) = DefaultParameters.BrightnessBounds

    var NorthSpreadChance: Double = DefaultParameters.NorthSpreadChance
    var EastSpreadChance: Double  = DefaultParameters.EastSpreadChance
    var SouthSpreadChance: Double = DefaultParameters.SouthSpreadChance
    var WestSpreadChance: Double  = DefaultParameters.WestSpreadChance

    // unused for now
    var NorthSpreadChanceDelta: Double = DefaultParameters.NorthSpreadChanceDelta
    var EastSpreadChanceDelta: Double  = DefaultParameters.EastSpreadChanceDelta
    var SouthSpreadChanceDelta: Double = DefaultParameters.SouthSpreadChanceDelta
    var WestSpreadChanceDelta: Double  = DefaultParameters.WestSpreadChanceDelta

}
