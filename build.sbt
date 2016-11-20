name := "simple-scalatra-rest-service"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

resolvers ++= Seq(
    "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
    val scalatraVersion = "2.3.0"
    val jettyVersion = "9.3.0.M1"
    val slf4jVersion = "1.7.7"
    Seq(
        "org.scalatra"              %%  "scalatra"         % scalatraVersion,
        "org.scalatra"              %%  "scalatra-json"    % scalatraVersion,
        "org.json4s"                %%  "json4s-jackson"   % "3.2.9",
        "org.eclipse.jetty"         %   "jetty-server"     % jettyVersion,
        "org.eclipse.jetty"         %   "jetty-webapp"     % jettyVersion,
        "org.slf4j"                 %   "slf4j-api"        % slf4jVersion,
        "org.slf4j"                 %   "slf4j-simple"     % slf4jVersion
    )
}

mainClass := Some("SimpleScalatraRestService")