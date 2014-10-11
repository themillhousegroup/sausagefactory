name := "sausagefactory"

version := "0.4.0"

scalaVersion := "2.11.2"

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "com.google.gdata"      %   "core"                  % "1.47.1",
    "org.mockito"           %   "mockito-all"           % "1.9.0",
    "org.scala-lang"        %   "scala-reflect"         % scalaVersion.value,
    "org.specs2"            %%  "specs2"                % "2.3.12"              % "test"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := Some("Cloudbees releases" at "https://repository-themillhousegroup.forge.cloudbees.com/"+ "release")

scalariformSettings



