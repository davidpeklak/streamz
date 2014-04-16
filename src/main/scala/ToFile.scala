
import java.io.FileWriter
import java.sql.{ResultSet, Connection}
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object ToFile {

  def writeFile(path: String): Sink[Task, String] = resource {
    Task.delay(new FileWriter(path))
  } {
    fw => Task.delay(fw.close())
  } {
    fw => Task.now(s => Task.delay(fw.write(s + "\n")))
  }

  import FromDb._
  import Tee._
  import Comp._

  val pr1 = (readDb(Db1.connect _) tee readDb(Db2.connect _))(matchTee)
    .filter(t => !equal(t))
    .map(info) to writeFile("target/diffs.txt")

}
