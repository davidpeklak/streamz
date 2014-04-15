import smt._

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.4",
  "org.scalaz.stream" %% "scalaz-stream" % "0.3.1"
)

database in dev := new H2Database({
  Class.forName("org.h2.Driver")
  java.sql.DriverManager.getConnection("jdbc:h2:~/test_dev")
})

migrations <<= (migrationsSource) map ( m => Seq(
  SchemaMigration("0", m / "0"),
  SepSchemaMigration("|||", "setupTables", m / "setupTables")
))

transformations in dev := Seq()

allowRollback in dev := true
