lazy val playSlickV = "5.0.0-M5"

lazy val server = project
  .enablePlugins(PlayScala)
  .settings(
    inThisBuild(Seq(
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.13.0",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-slick-evolutions" % playSlickV,
        "org.postgresql" % "postgresql" % "42.2.6",
        "com.h2database" % "h2" % "1.4.199"
      )
    )),
    libraryDependencies ++= Seq(
      guice,
      "io.monix" %% "monix" % "3.0.0-RC3",
      "com.typesafe.play" %% "play-slick" % playSlickV,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    )
  )
  .aggregate(`run-evolutions`)

lazy val `run-evolutions` = (project in file("."))
  .enablePlugins(PlayScala)
