import java.io.InputStream
import java.sql.ResultSet
import scala.util.Try

object Util {

  def YELLOW = 27.toChar + "[0;33m"

  def RED = 27.toChar + "[0;31m"

  def NORMAL = 27.toChar + "[m"

  def splitString(string: String, separator: Char): IndexedSeq[String] = {
    def doSplit(acc: List[String], c: Char): List[String] = {
      if (c == separator) "" :: acc
      else {
        acc.headOption.map(head => (c + head) :: acc.tail).getOrElse(List(c.toString))
      }
    }
    string.toCharArray.foldLeft[List[String]]("" :: Nil)(doSplit).map(_.reverse).reverse.toIndexedSeq
  }

  def emptyToNone(s: String): Option[String] = if (s.isEmpty) None else Some(s)

  def inputStream(path: String): InputStream = Try {
    this.getClass.getResourceAsStream(path)
  }.getOrElse(new java.io.FileInputStream(path))

  implicit class RichResultOps(rs: ResultSet) {
    private def getOpt[T](f: ResultSet => T): Option[T] = {
      val t = f(rs)
      val n = rs.wasNull()
      if (n) None
      else Some(t)
    }

    def getOptDouble(columnLabel: String): Option[Double] = getOpt[Double](rs => rs.getDouble(columnLabel))

    def getOptString(columnLabel: String): Option[String] = Option(rs.getString(columnLabel))

    def getOptInt(columnLabel: String): Option[Int] = getOpt[Int](rs => rs.getInt(columnLabel))

    def getOptLong(columnLabel: String): Option[Long] = getOpt[Long](rs => rs.getLong(columnLabel))
  }

  implicit class InstrTuplOps(und: (Option[Instrument], Option[Instrument])) {
    private def ioToString(io: Option[Instrument]) = io match {
      case Some(i) => i.toString
      case None => RED + "None" + NORMAL
    }


    def toStringo: String = "(" + ioToString(und._1) + ", " + ioToString(und._2) + ")"
  }
}
