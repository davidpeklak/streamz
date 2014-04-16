import java.sql.{ResultSet, Connection}
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.io._
import scalaz.stream.Process
import Util._

object Tee {

  val i1: Process[Task, Instrument] = emitAll(Seq(
    Instrument(1, None, Some(100000)),
    Instrument(3, None, Some(1000)),
    Instrument(4, None, Some(10000)),
    Instrument(5, None, Some(1))
  ))

  val i2: Process[Task, Instrument] = emitAll(Seq(
    Instrument(1, None, Some(100000)),
    Instrument(2, None, Some(10000)),
    Instrument(3, None, Some(1000)),
    Instrument(4, None, Some(666))
  ))

  def bothTee[I]: Tee[I, I, (Option[I], Option[I])] = {
    def go: Tee[I, I, (Option[I], Option[I])] = {
      receiveL[I, I, (Option[I], Option[I])](l => receiveR[I, I, (Option[I], Option[I])](r => Emit(Seq((Some(l), Some(r))), go)))
    }

    go ++
      awaitL[I].map(l => (Some(l), None)).repeat ++
      awaitR[I].map(l => (None, Some(l))).repeat
  }

  val pr1 = (i1 tee i2)(bothTee).map(_.toStringo) to stdOutLines

  implicit val oi: Ordering[Instrument] = Ordering.by(_.sicovam)

  def matchTee[I: Ordering]: Tee[I, I, (Option[I], Option[I])] = {
    val ord = implicitly[Ordering[I]]

    def go(cl: I, cr: I): Tee[I, I, (Option[I], Option[I])] = {
      if (ord.lt(cl, cr)) Emit(List((Some(cl), None)), receiveLOr[I, I, (Option[I], Option[I])](emit((None, Some(cr))))(go(_, cr)))
      else if (ord.gt(cl, cr)) Emit(List((None, Some(cr))), receiveROr[I, I, (Option[I], Option[I])](emit((Some(cl), None)))(go(cl, _)))
      else Emit(List((Some(cl), Some(cr))), receiveL[I, I, (Option[I], Option[I])](l => receiveROr[I, I, (Option[I], Option[I])](emit((Some(l), None)))(r => go(l, r))))
    }

    receiveL[I, I, (Option[I], Option[I])](l => receiveROr[I, I, (Option[I], Option[I])](emit((Some(l), None)))(r => go(l, r))) ++
      awaitL[I].map(l => (Some(l), None)).repeat ++
      awaitR[I].map(l => (None, Some(l))).repeat
  }

  val pr2 = (i1 tee i2)(matchTee).map(_.toStringo) to stdOutLines

  import FromDb._

  val pr3 = (readDb(Db1.connect _) tee readDb(Db2.connect _))(matchTee).map(_.toStringo) to stdOutLines
}
