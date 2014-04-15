import sbt._
import sbt.Keys._
import java.io.File
import smt.SMT

object MyBuild extends Build {

  lazy val proj = Project(id = "smt-regreport",
    base = new File("."),
    settings = Project.defaultSettings ++ SMT.globalSmtSettings ++ inConfig(dev)(SMT.smtSettings)
  )

  lazy val dev = config("dev")

}