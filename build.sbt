lazy val playSlickV = "3.0.3"

lazy val server = project
  .enablePlugins(PlayScala)
  .settings(
    inThisBuild(Seq(
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.12.8",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-slick-evolutions" % playSlickV,
        "org.postgresql" % "postgresql" % "42.2.2",
        "com.h2database" % "h2" % "1.4.197"
      )
    )),
    libraryDependencies ++= Seq(
      guice,
      "io.monix" %% "monix" % "3.0.0-RC1",
      "com.typesafe.play" %% "play-slick" % playSlickV,

      "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
      "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
      "org.mockito" % "mockito-core" % "2.19.0" % Test
    )
  )
  .aggregate(`run-evolutions`)

lazy val `run-evolutions` = (project in file("."))
  .enablePlugins(PlayScala)
