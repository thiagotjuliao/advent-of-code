ThisBuild / scalaVersion := "3.9.0"
ThisBuild / organization := "dev.thiagojuliao"
ThisBuild / version := "0.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "advent-of-code",
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Wunused:imports"
    ),
    libraryDependencies += "org.scalameta" %% "munit" % "1.3.6" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / mainClass := Some("aoc.Runner"),
    run / fork := true,
    run / connectInput := true,
    // The AoC loves deep recursion and large grids
    run / javaOptions ++= Seq("-Xss512m", "-Xmx4g")
  )
