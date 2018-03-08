lazy val playSlickV = "3.0.3"

lazy val server = project
  .enablePlugins(PlayScala)
  .settings(
    inThisBuild(Seq(
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.12.4",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-slick-evolutions" % playSlickV,
        "org.postgresql" % "postgresql" % "42.2.1",
        "com.h2database" % "h2" % "1.4.196"
      )
    )),
    libraryDependencies ++= Seq(
      guice,
      "io.monix" %% "monix" % "3.0.0-M3",
      "com.typesafe.play" %% "play-slick" % playSlickV,
      "org.elasticsearch.client" % "elasticsearch-rest-client" % "6.2.2",

      "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
      "org.mockito" % "mockito-core" % "2.15.0" % Test
    )
  )
  .aggregate(`run-evolutions`)

lazy val `run-evolutions` = (project in file("."))
  .enablePlugins(PlayScala)
