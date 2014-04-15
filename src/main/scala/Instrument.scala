import java.sql.ResultSet
import Util._

case class Instrument(
                       sicovam: Int,
                       theType: Option[String],
                       notional: Option[Double]
                       ) {
  override def toString: String = {
    "Instrument(" +
    YELLOW + sicovam.toString + NORMAL + ", " +
    theType.getOrElse("None") + ", " +
    notional.getOrElse("None") + ")"
  }
}

object Instrument {
  def fromString(s: String): Instrument = {
    val splits = splitString(s, ';')

    Instrument(splits(0).toInt, emptyToNone(splits(1)), emptyToNone(splits(2)).map(_.toDouble))
  }
}