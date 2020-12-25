name := "http4s-crud"
ThisBuild / organization := "com.juliasoft"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.4"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := V.SemanticDbVersion

addCompilerPlugin("org.typelevel" % "kind-projector_2.13.4" % "0.11.2")
addCompilerPlugin("org.scalameta" % "semanticdb-scalac"  % V.SemanticDbVersion cross CrossVersion.full)
addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1")

libraryDependencies ++= Seq(
  "org.http4s"        %% "http4s-blaze-server" % V.Http4sVersion,
  "org.http4s"        %% "http4s-blaze-client" % V.Http4sVersion,
  "org.http4s"        %% "http4s-circe"        % V.Http4sVersion,
  "org.http4s"        %% "http4s-dsl"          % V.Http4sVersion,
  "org.typelevel"     %% "cats-core"           % V.CatsVersion,
  "org.typelevel"     %% "cats-effect"         % V.CatsVersion,
  "org.tpolecat"      %% "doobie-core"         % V.DoobieVersion,
  "org.tpolecat"      %% "doobie-h2"           % V.DoobieVersion,
  "org.tpolecat"      %% "doobie-hikari"       % V.DoobieVersion,
  "io.circe"          %% "circe-core"          % V.CirceVersion,
  "io.circe"          %% "circe-generic"       % V.CirceVersion,
  "io.circe"          %% "circe-config"        % V.CirceConfigVersion,
  "org.scalameta"     %% "svm-subs"            % V.GraalVMVersion,
  "com.h2database"     % "h2"                  % V.H2Version            % Test,
  "org.tpolecat"      %% "doobie-scalatest"    % V.DoobieVersion        % Test,
  "org.scalatest"     %% "scalatest"           % V.ScalatestVersion     % Test,
  "org.scalatestplus" %% "scalacheck-1-15"     % V.ScalatestPlusVersion % Test,
  "ch.qos.logback"     % "logback-classic"     % V.LogbackVersion
)
