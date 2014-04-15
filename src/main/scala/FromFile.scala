import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object FromFile {

  def read(path: String): Process[Task, String] = resource {
    Task.delay(Source.fromInputStream(inputStream(path)))
  } {
    src => Task.delay(src.close())
  } {
    src =>
      lazy val lines = src.getLines()
      Task.delay {
        if (lines.hasNext) lines.next() else throw End
      }
  }

  val readInstr = read("Instr1.txt")

  val pr1 = readInstr to stdOutLines

  val pr2 = readInstr.map(Instrument.fromString).map(_.toString) to stdOutLines

  val pr3 = readInstr.map(Instrument.fromString).filter(_.theType == Some("I")).map(_.toString) to stdOutLines
}
