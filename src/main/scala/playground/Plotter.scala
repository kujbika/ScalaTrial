package playground
import plotly._
import plotly.Bar
import Plotly._

/**This object is able to plot in a plotly format!
  * https://github.com/alexarchambault/plotly-scala
  */
object Plotter extends App {
  val (x, y) = Seq(
    "Banana" -> 10,
    "Apple" -> 8,
    "Grapefruit" -> 5
  ).unzip

  Bar(x, y).plot(openInBrowser =
    false
  ) // this is neccesary for not breaking the sbt terminal
}
