import java.sql.Connection
import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object ToDb {
  import FromFile.read

  def writeInstrument(c: Connection, i: Instrument) {
    val ps = c.prepareStatement("insert into INSTR (SICOVAM, TYPE_, NOTIONAL) values (?, ?, ?)")
    ps.setInt(1, i.sicovam)
    ps.setString(2, i.theType.orNull)
    i.notional match {
      case Some(n) => ps.setDouble(3, n)
      case None => ps.setNull(3, java.sql.Types.DOUBLE)
    }

    ps.execute()
    ps.close()
  }

  def writeDb(cc: () => Connection): Sink[Task, Instrument] = resource{
    Task.delay(cc())
  } {
    c => Task.delay(c.close())
  } {
    c => Task.now(i => Task.delay(writeInstrument(c, i)))
  }

  val pr1 = read("Instr1.txt").map(Instrument.fromString) to writeDb(Db1.connect _)
  val pr2 = read("Instr2.txt").map(Instrument.fromString) to writeDb(Db2.connect _)
}
