package me.paul.artgenerators

import javafx.scene.paint.Color

case class Pixel(x: Int, y: Int)
case class PixelColor(color: Option[Color] = None)