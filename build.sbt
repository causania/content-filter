organization := "org.koderama"

name := "fair-use"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"


// Compile
libraryDependencies ++= {
  Seq(
    "org.encog" % "encog-core" % "3.0.1" withSources(),
    "ch.qos.logback" % "logback-classic" % "1.0.0"
  )
}

// Provided
libraryDependencies ++= {
  Seq(
  )
}

// Test
libraryDependencies ++= {
  Seq(
    "org.specs2" %% "specs2" % "1.6.1" withSources(),
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test" withSources(),
    "org.mockito" % "mockito-all" % "1.9.0-rc1" % "test" withSources(),
    "se.scalablesolutions.akka" % "akka-actor" % "1.3-RC1" withSources()
  )
}

resolvers ++= Seq(
  "snapshots" at "http://scala-tools.org/repo-snapshots",
  "releases" at "http://scala-tools.org/repo-releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Coda Hale's Repository" at "http://repo.codahale.com/"
)
