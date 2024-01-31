val scala3Version = "3.0.0-RC1"


lazy val root = project
  .in(file("."))
  .settings(
    name := "topt-poc",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test", 
    libraryDependencies += "dev.samstevens.totp" % "totp" % "1.7.1" 
  )
