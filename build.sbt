import smt._

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.172",
  "org.scalaz" %% "scalaz-core" % "7.0.4",
  "org.scalaz.stream" %% "scalaz-stream" % "0.3.1"
)

database in dev1 := new H2Database({
  Class.forName("org.h2.Driver")
  java.sql.DriverManager.getConnection("jdbc:h2:~/test_dev")
})

database in dev2 := new H2Database({
  Class.forName("org.h2.Driver")
  java.sql.DriverManager.getConnection("jdbc:h2:~/test_dev2")
})

migrations <<= (migrationsSource) map ( m => Seq(
  SchemaMigration("0", m / "0"),
  SepSchemaMigration("|||", "setupTables", m / "setupTables")
))

transformations in dev1 := Seq()

allowRollback in dev1 := true

transformations in dev2 := Seq()

allowRollback in dev2 := true