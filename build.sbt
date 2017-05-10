lazy val playSlickV = "3.0.3"

lazy val `rdb-to-elastic` = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-slick" % playSlickV,
      "com.typesafe.play" %% "play-slick-evolutions" % playSlickV,
      "org.postgresql" % "postgresql" % "42.1.4",
      "com.h2database" % "h2" % "1.4.196",
      "org.elasticsearch.client" % "rest"  % "6.0.0-alpha2",

      "org.scalatest" %% "scalatest" % "3.0.4" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
      "org.mockito" % "mockito-core" % "2.13.0" % Test
    )
  )
