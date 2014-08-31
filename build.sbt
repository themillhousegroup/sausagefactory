name := "sausagefactory"

version := "0.1.0"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.11.2", "2.10.4")

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "com.google.gdata"      %   "core"                  % "1.47.1",
    "ch.qos.logback"        %   "logback-classic"       % "1.1.2",
    "org.mockito"           %   "mockito-all"           % "1.9.0",
    "org.specs2"            %%  "specs2"                % "2.3.12"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := Some("Cloudbees releases" at "https://repository-themillhousegroup.forge.cloudbees.com/"+ "release")

scalariformSettings



