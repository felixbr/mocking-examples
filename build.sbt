Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.13.4"
      )
    ),
    name := "mocking-examples",
    version := "0.1.0",
    libraryDependencies ++= (
      List(
        Dependencies.monix,
      ) ++ Dependencies.logging.viaLog4j2 ++ Dependencies.circe.noGeneric
    ).map(_.withDottyCompat(scalaVersion.value)),
    libraryDependencies ++= List(
      Dependencies.testing.scalaTest, // Scalatest uses macros, so we cannot simply use the ".withDottyCompat" solution but need actual cross-built versions
      Dependencies.testing.scalaCheck.withDottyCompat(scalaVersion.value),
      Dependencies.testing.scalaTestPlusScalaCheck.intransitive().withDottyCompat(scalaVersion.value),
      Dependencies.testing.scalaTestPlusMockito
    )
  )
  .enablePlugins(JavaAppPackaging)

// format: off
scalacOptions ++= List( // useful compiler flags for scala
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-language:implicitConversions"
)
// format: on

addCommandAlias("scalafmtFormatAll", "; root/scalafmtAll ; root/scalafmtSbt")
addCommandAlias("scalafmtValidateAll", "; root/scalafmtCheckAll ; root/scalafmtSbtCheck")
