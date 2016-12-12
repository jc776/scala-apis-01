name := "Example"
version := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.1",
  "com.chuusai" %%% "shapeless" % "2.3.2",
  "com.lihaoyi" %%% "upickle" % "0.4.3"
)

jsDependencies ++= Seq(
  ProvidedJS / "morphdom-wzrd.js"
)

ensimeIgnoreMissingDirectories in ThisBuild := true
