lazy val appSettings = Seq(
  organization := "com.hart",
  name := "scala-etl",
  version := "1.0.0-SNAPSHOT"
)

// Those settings should be the same as in alchemy!
lazy val scalaVersion_ = "2.11.8"
lazy val javaVersion = "1.8"
lazy val sparkVersion = "2.1.0"

lazy val scalaCheckVersion = "1.12.5"
lazy val scalaTestVersion = "2.2.5"

scalaVersion in Global := scalaVersion_

scalacOptions in Global ++= Seq(
  "-deprecation",
  "-feature",
  "-target:jvm-" + javaVersion,
  "-Xlint"
)

javacOptions in Global ++= Seq(
  "-encoding", "UTF-8",
  "-source", javaVersion,
  "-target", javaVersion
)

lazy val sparkLib = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided withSources() withJavadoc(),
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided withSources() withJavadoc()
)

lazy val auxLib = Seq(
  // apache common
  "commons-codec" % "commons-codec" % "1.10" % Provided,
  "org.apache.commons" % "commons-lang3" % "3.3.2" % Provided withSources(),
  // parsing
  "com.databricks" % "spark-csv_2.11" % "1.5.0" withSources(),
  // utils
  "org.slf4j" % "slf4j-api" % "1.7.10" % Provided withSources()
)

lazy val testLib = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test withSources() withJavadoc()
)

//removes _2.10 auto suffix in artifact name
crossPaths in Global := false

publishMavenStyle in Global := true

net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val root = (project in file("."))
  .settings(appSettings: _*)
  .settings(
    libraryDependencies ++= sparkLib,
    libraryDependencies ++= auxLib,
    libraryDependencies ++= testLib,
    dependencyOverrides ++= Set(
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.4.4" % Provided, // to avoid mix of jackson jars versions
      "com.fasterxml.jackson.core" % "jackson-core" % "2.4.4" % Provided,
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4" % Provided,
      "org.apache.httpcomponents" % "httpcore" % "4.3.3",
      "org.apache.httpcomponents" % "httpclient" % "4.3.6",
      "commons-beanutils" % "commons-beanutils" % "1.8.3",
      "commons-codec" % "commons-codec" % "1.10",
      "commons-lang" % "commons-lang" % "2.6",
      "com.google.guava"  % "guava" % "14.0.1",
      "joda-time" % "joda-time" % "2.9")
  )
  .settings(addArtifact(artifact in (Compile, assembly), assembly).settings: _*)

fork in Global := true

