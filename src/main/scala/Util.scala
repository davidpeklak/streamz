import java.io.InputStream
import scala.util.Try

object Util {
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

  def inputStream(path: String): InputStream =  Try { this.getClass.getResourceAsStream(path) }.getOrElse(new java.io.FileInputStream(path))
}
