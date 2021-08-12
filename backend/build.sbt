val finchVersion = "0.26.0"
val circeVersion = "0.10.1"
val postgresqlVersion = "42.2.18"
val bcryptVersion = "4.1"

lazy val root = (project in file("."))
  .settings(
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.postgresql" % "postgresql" % postgresqlVersion,
      "com.github.t3hnar" %% "scala-bcrypt" % bcryptVersion
    )
  )