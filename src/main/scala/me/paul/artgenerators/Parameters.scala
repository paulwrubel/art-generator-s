package me.paul.artgenerators

import java.io.File

class Parameters {

    var Debug: Boolean = _
    var Version: String = _

    var ImageCount: Int = _
    var OpenFile: Boolean = _

    var Width: Int  = _
    var Height: Int = _

    var Filename: String = _
    var Filepath: File = _

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

    override def toString: String = {
        val s: String = {
            "Debug = " + Debug + "\n" +
                    "Version = " + Version + "\n" +
                    "ImageCount = " + ImageCount + "\n" +
                    "OpenFile = " + OpenFile + "\n" +
                    "Width = " + Width + "\n" +
                    "Height = " + Height + "\n" +
                    "Filename = " + Filename + "\n" +
                    "Filepath = " + Filepath + "\n" +
                    "FileFormat = " + FileFormat + "\n" +
                    "SeedCount = " + SeedCount + "\n" +
                    "HueVariation = " + HueVariation + "\n" +
                    "SaturationVariation = " + SaturationVariation + "\n" +
                    "BrightnessVariation = " + BrightnessVariation + "\n" +
                    "HueVariationDelta = " + HueVariationDelta + "\n" +
                    "SaturationVariationDelta = " + SaturationVariationDelta + "\n" +
                    "BrightnessVariationDelta = " + BrightnessVariationDelta + "\n" +
                    "HueBounds = " + HueBounds + "\n" +
                    "SaturationBounds = " + SaturationBounds + "\n" +
                    "BrightnessBounds = " + BrightnessBounds + "\n" +
                    "NorthSpreadChance = " + NorthSpreadChance + "\n" +
                    "EastSpreadChance = " + EastSpreadChance + "\n" +
                    "SouthSpreadChance = " + SouthSpreadChance + "\n" +
                    "WestSpreadChance = " + WestSpreadChance + "\n" +
                    "NorthSpreadChanceDelta = " + NorthSpreadChanceDelta + "\n" +
                    "EastSpreadChanceDelta = " + EastSpreadChanceDelta + "\n" +
                    "SouthSpreadChanceDelta = " + SouthSpreadChanceDelta + "\n" +
                    "WestSpreadChanceDelta = " + WestSpreadChanceDelta
        }
        s
    }

}
