import java.sql.{ResultSet, Connection}
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object Par {
  val p1: Process[Task, Int] = emitAll(1 to 10)

  def expensiveMult(i: Int): Int = {
    println("Multiply " + i + " on thread " + Thread.currentThread().getId)
    Thread.sleep(2L * 1000L)
    val j = 2*i
    println("Done " + i + " on thread " + Thread.currentThread().getId)
    j
  }

  lazy val p2 = p1.map(expensiveMult).map(_.toString) to stdOutLines

  lazy val p3 = p1.evalMap(i => Task.fork(Task.delay(expensiveMult(i)))).map(_.toString) to stdOutLines

  lazy val p4 = p1.gatherMap(500)(i => Task.fork(Task.delay(expensiveMult(i)))).map(_.toString) to stdOutLines

}
