name := "sausagefactory"

version := s"${sys.props.getOrElse("build.majorMinor", "0.4")}.${sys.props.getOrElse("build.version", "SNAPSHOT")}"

scalaVersion := "2.11.7"

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "com.google.gdata"      %   "core"                  % "1.47.1",
    "org.mockito"           %   "mockito-all"           % "1.9.5",
    "org.scala-lang"        %   "scala-reflect"         % scalaVersion.value,
    "org.specs2"            %%  "specs2"                % "2.3.13"              % "test"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

seq(bintraySettings:_*)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalariformSettings



