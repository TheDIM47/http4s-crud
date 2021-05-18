name := "http4s-crud"
ThisBuild / organization := "com.juliasoft"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := V.SemanticDbVersion

addCompilerPlugin("org.typelevel" %% "kind-projector"     % V.kindProjector     cross CrossVersion.full)
addCompilerPlugin("org.scalameta"  % "semanticdb-scalac"  % V.SemanticDbVersion cross CrossVersion.full)
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

enablePlugins(NativeImagePlugin)

Compile / mainClass := Some("com.juliasoft.crud.Main")

// nativeImageVersion := "21.0.0"
// nativeImageGraalHome := file("/opt/graalvm-ce-java11-21.0.0").toPath
// nativeImageInstalled := true

nativeImageOptions ++= List(
  "-H:+AddAllCharsets",
  "-H:ResourceConfigurationFiles=../../src/main/resources/resources-config.json",
  "--no-fallback",
  "--allow-incomplete-classpath",
  "--enable-http",
  "--enable-https",
  "--initialize-at-build-time",
  "--initialize-at-run-time=com.typesafe.config.impl.ConfigImpl$EnvVariablesHolder",
  "--initialize-at-run-time=com.typesafe.config.impl.ConfigImpl$SystemPropertiesHolder",
  "--initialize-at-run-time=io.netty.buffer.PooledByteBufAllocator",
  "--initialize-at-run-time=org.h2.store.fs.FileNioMemData"
)

libraryDependencies ++= Seq(
  "org.http4s"        %% "http4s-blaze-server" % V.Http4sVersion,
  "org.http4s"        %% "http4s-blaze-client" % V.Http4sVersion,
  "org.http4s"        %% "http4s-circe"        % V.Http4sVersion,
  "org.http4s"        %% "http4s-dsl"          % V.Http4sVersion,
  "org.typelevel"     %% "cats-core"           % V.CatsVersion,
  "org.typelevel"     %% "cats-effect"         % V.CatsEffectVersion,
  "org.tpolecat"      %% "doobie-core"         % V.DoobieVersion,
  "org.tpolecat"      %% "doobie-h2"           % V.DoobieVersion,
  "org.tpolecat"      %% "doobie-hikari"       % V.DoobieVersion,
  "io.circe"          %% "circe-core"          % V.CirceVersion,
  "io.circe"          %% "circe-generic"       % V.CirceVersion,
  "io.circe"          %% "circe-config"        % V.CirceConfigVersion,
  "org.scalameta"      % "svm-subs"            % V.SVMVersion,
  "com.h2database"     % "h2"                  % V.H2Version            % Test,
  "org.tpolecat"      %% "doobie-scalatest"    % V.DoobieVersion        % Test,
  "org.scalatest"     %% "scalatest"           % V.ScalatestVersion     % Test,
  "org.scalatestplus" %% "scalacheck-1-15"     % V.ScalatestPlusVersion % Test,
  "ch.qos.logback"     % "logback-classic"     % V.LogbackVersion
)
