package me.paul.artgenerators

class Parameters {

    var Debug: Boolean = _
    var Version: String = _

    var ImageCount: Int = _
    var OpenFile: Boolean = _

    var Width: Int  = _
    var Height: Int = _

    var Filename: String = _
    var Filepath: String = _

    var FileFormat: String = _

    var SeedCount: Int = _

    var HueVariation: Double        = _
    var SaturationVariation: Double = _
    var BrightnessVariation: Double = _

    // unused for now
    var HueVariationDelta: Double        = _
    var SaturationVariationDelta: Double = _
    var BrightnessVariationDelta: Double = _

    var HueBounds: (Double, Double)        = _
    var SaturationBounds: (Double, Double) = _
    var BrightnessBounds: (Double, Double) = _

    var NorthSpreadChance: Double = _
    var EastSpreadChance: Double  = _
    var SouthSpreadChance: Double = _
    var WestSpreadChance: Double  = _

    var NorthSpreadChanceDelta: Double = _
    var EastSpreadChanceDelta: Double  = _
    var SouthSpreadChanceDelta: Double = _
    var WestSpreadChanceDelta: Double  = _
}
