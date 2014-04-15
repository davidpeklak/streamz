import java.sql.{ResultSet, Connection}
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object Comp {

  import FromDb._
  import Tee._

  val pr = (readDb(Db1.connect _) tee readDb(Db2.connect _))(matchTee)

  def equal(t: (Option[Instrument], Option[Instrument])): Boolean = t match {
    case (Some(i), Some(j)) if i == j => true
    case (None, None) => true
    case _ => false
  }

  val pr1 = pr.filter(t => !equal(t)).map(_.toStringo) to stdOutLines

  def info(in: (Option[Instrument], Option[Instrument])): String = in match {
    case (None, None) => "None"
    case (Some(i), None) => "Missing " + i.sicovam.toString
    case (None, Some(i)) => "Additional " + i.sicovam.toString
    case (Some(i), Some(j)) => {
      def diff[T](field: Instrument => Option[T]): String = {
        val f1 = field(i).map(_.toString).getOrElse("None")
        val f2 = field(j).map(_.toString).getOrElse("None")
        if (f1 == f2) f1
        else f1 + "||||" + f2
      }

      i.sicovam.toString + ", " +
        diff(_.theType) + ", " +
        diff(_.notional)
    }
  }

  val pr2 = pr.filter(t => !equal(t)).map(info) to stdOutLines

}
