lazy val root = (project in file(".")).
  settings(
    name := "slickz",
    version := "0.1.1",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation"
    ),
    organization := "com.github.2chilled"
  ).settings(dependencySettings, publishSettings)

lazy val dependencySettings = {
  val scalazVersion = "7.1.3"

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "2.2.5",
    "org.scalacheck" %% "scalacheck" % "1.12.4",
    "com.h2database" % "h2" % "1.4.187"
  ).map(_ % "test")

  Seq(
    resolvers ++= Seq(
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      Resolver.url("Edulify Repository", url("http://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
    ),
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion,
      "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
      "com.typesafe.slick" %% "slick" % "2.1.0",
      "org.slf4j" % "slf4j-nop" % "1.6.4"
    ) ++ testDependencies
  )
}

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := (
    <url>https://github.com/2chilled/slickz</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/bsd-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:2chilled/slickz.git</url>
        <connection>scm:git:git@github.com:2chilled/slickz.git</connection>
      </scm>
      <developers>
        <developer>
          <id>mherrmann</id>
          <name>Matthias Herrmann</name>
        </developer>
      </developers>
    )
)

