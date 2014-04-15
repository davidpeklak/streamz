import java.sql.{ResultSet, Connection}
import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object FromDb {

  def fromResultSet(rs: ResultSet): Instrument = {
    Instrument(
      rs.getInt("SICOVAM"),
      rs.getOptString("TYPE_"),
      rs.getOptDouble("NOTIONAL")
    )
  }

 def readDb(cc: () => Connection): Process[Task, Instrument] = resource {
   Task.delay {
     val c = cc()
     val ps = c.prepareStatement("select * from INSTR order by SICOVAM")
     val rs = ps.executeQuery()
     (c, ps, rs)
   }
 }{
     case (c, ps, rs) =>
       Task.delay {
         rs.close()
         ps.close()
         c.close()
       }
   } {
     case (c, ps, rs) =>
       Task.delay {
         if (rs.next) fromResultSet(rs) else throw End
       }
   }

  val pr1 = readDb(Db1.connect _).map(_.toString) to stdOutLines
  val pr2 = readDb(Db2.connect _).map(_.toString) to stdOutLines

}
