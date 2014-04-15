import sbt._
import sbt.Keys._
import java.io.File
import smt.SMT

object MyBuild extends Build {

  lazy val proj = Project(id = "smt-regreport",
    base = new File("."),
    settings = Project.defaultSettings ++ SMT.globalSmtSettings ++ inConfig(dev1)(SMT.smtSettings) ++ inConfig(dev2)(SMT.smtSettings)
  )

  lazy val dev1 = config("dev1")

  lazy val dev2 = config("dev2")

}