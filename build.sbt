organization := "ap.test"

name := "jmdns"

libraryDependencies += "javax.jmdns" % "jmdns" % "3.4.1"

libraryDependencies += "com.typesafe.akka" % "akka-osgi_2.10" % "2.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

libraryDependencies += "biz.aQute" % "bnd" % "1.50.0"

osgiSettings

OsgiKeys.privatePackage := Seq("ap.test.jmdns")

OsgiKeys.importPackage := Seq("!aQute.*, *")

OsgiKeys.bundleActivator := Option("ap.test.jmdns.SharedAkka")

//OsgiKeys.embeddedJars := Seq("jmdns-3.4.1.jar")

OsgiKeys.additionalHeaders := Map("Service-Component" -> "*", 
                                  "Include-Resource" -> "@jmdns-3.4.1.jar")
