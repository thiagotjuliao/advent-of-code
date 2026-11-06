ThisBuild / scalaVersion := "3.9.0"
ThisBuild / organization := "dev.thiagojuliao"
ThisBuild / version := "0.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "advent-of-code",
    // Tests run from the class directories, not from jars. With sbt 2's
    // default `exportJars := true` the test classpath is the packaged jar, and
    // when compile is served from the disk cache (sbt 2.0.9) the jar is not
    // repackaged: undoing an edit ran the tests against the last real compile,
    // and `clean testFull` ran no tests and reported success. Measured
    // 2026-10-02; keep it in every module of a multi-module build.
    exportJars := false,
    scalacOptions ++= CompilerFlags.base,
    libraryDependencies += "org.scalameta" %% "munit" % "1.3.6" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / mainClass := Some("aoc.Runner"),
    run / fork := true,
    run / connectInput := true,
    // The AoC loves deep recursion and large grids
    run / javaOptions ++= Seq("-Xss512m", "-Xmx4g")
  )

// The one definition of "green", run by CI and worth running before a push:
// every source formatted, and every test run.
//
// `testFull`, not `test`: under sbt 2 `test` is incremental — it runs what
// failed last time, what never ran, and what a changed dependency touched, and
// nothing else. Its greenest possible run is the one that ran no tests at all.
addCommandAlias("verify", "scalafmtCheckAll; scalafmtSbtCheck; testFull")
