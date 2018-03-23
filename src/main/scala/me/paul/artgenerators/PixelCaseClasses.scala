package me.paul.artgenerators

import javafx.scene.paint.Color

case class Pixel(x: Int, y: Int)
case class PixelData(color: Option[Color] = None, parent: Option[Pixel] = None)