addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

resolvers += "spray repo" at "http://repo.spray.io"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
addSbtPlugin("com.lihaoyi" % "workbench" % "0.3.0")

addSbtPlugin("org.ensime" % "sbt-ensime" % "1.12.4")
