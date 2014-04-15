resolvers += Resolver.url( "smt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)

resolvers += "Nexus Releases" at "http://nexus/nexus/content/groups/public"

addSbtPlugin("com.github.davidpeklak" % "smt" % "0.3i-SNAPSHOT")

