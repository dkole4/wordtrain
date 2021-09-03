val finchVersion = "0.32.1"
val circeVersion = "0.14.1"
val postgresqlVersion = "42.2.18"
val finaglePSQLVersion = "0.13.0"
val bcryptVersion = "4.3.0"

lazy val root = (project in file("."))
  .settings(
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.github.finagle" %% "finagle-postgres-shapeless" % finaglePSQLVersion,
      "org.postgresql" % "postgresql" % postgresqlVersion,
      "com.github.t3hnar" %% "scala-bcrypt" % bcryptVersion
    )
  )