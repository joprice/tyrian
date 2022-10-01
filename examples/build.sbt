import scala.sys.process._
import scala.language.postfixOps
import org.scalajs.jsenv.nodejs.NodeJSEnv

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val tyrianVersion = TyrianVersion.getVersion
lazy val scala3Version = "3.2.0"

lazy val commonSettings: Seq[sbt.Def.Setting[_]] = Seq(
  version      := tyrianVersion,
  scalaVersion := scala3Version,
  organization := "io.indigoengine",
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % "0.7.29" % Test
  ),
  libraryDependencies ++= Seq(
    "io.indigoengine" %%% "tyrian-io" % tyrianVersion
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  crossScalaVersions := Seq(scala3Version),
  scalafixOnCompile  := true,
  semanticdbEnabled  := true,
  semanticdbVersion  := scalafixSemanticdb.revision,
  autoAPIMappings    := true
)

lazy val bootstrap =
  (project in file("bootstrap"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "bootstrap")

lazy val bundler =
  (project in file("bundler"))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(name := "bundler")
    .settings(
      // Source maps seem to be broken with bundler
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      scalaJSUseMainModuleInitializer := true
    )

lazy val clock =
  (project in file("clock"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "clock")

lazy val counter =
  (project in file("counter"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "counter")

lazy val `electron-server` =
  project
    .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterExternalNpmPlugin)
    .settings(commonSettings: _*)
    .settings(name := "electron")
    .settings(
      stStdlib                        := List("es5"), // doesn't include DOM
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= (_
        /* disabled because it somehow triggers many warnings */
        .withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule)),
      /** ScalablyTypedConverterExternalNpmPlugin requires that we define how to
        * install node dependencies and where they are
        *
        * Since we run yarn ourselves the dependencies live in
        * electron/package.json
        *
        * Invoking yarn is somewhat awkward, because for instance sbt started
        * through intellij won't have a proper PATH set. try to start an
        * interactive shell to read the necessary dot files
        */
      externalNpm := {
        if (scala.util.Properties.isWin)
          Process("yarn", baseDirectory.value).run()
        else Process("bash -ci 'yarn'", baseDirectory.value).run()
        baseDirectory.value
      },
      jsEnv := new NodeJSEnv(
        NodeJSEnv
          .Config()
          .withExecutable("electron-server/node_modules/.bin/electron")
          .withArgs(List((Compile / classDirectory).value.toString))
      )
    )

lazy val electron =
  (project in file("electron"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "electron",
      scalaJSLinkerConfig ~= (_
        /* disabled because it somehow triggers many warnings */
        .withSourceMap(false)
        .withModuleKind(ModuleKind.ESModule))
    )

lazy val field =
  (project in file("field"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "field")

lazy val fs2 =
  (project in file("fs2"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "fs2")

lazy val http =
  (project in file("http"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "http",
      libraryDependencies ++= Seq(
        "io.circe" %%% "circe-core",
        "io.circe" %%% "circe-parser"
      ).map(_ % "0.14.2")
    )

lazy val http4sdom =
  (project in file("http4s-dom"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "http4s-dom",
      libraryDependencies ++= Seq(
        "io.circe" %%% "circe-core",
        "io.circe" %%% "circe-parser",
        "io.circe" %%% "circe-generic"
      ).map(_ % "0.14.2")
    )
    .settings(
      libraryDependencies ++= Seq(
        "org.http4s" %%% "http4s-dom"   % "0.2.3",
        "org.http4s" %%% "http4s-circe" % "0.23.15"
      )
    )

lazy val indigo =
  (project in file("indigo"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "indigo-bridge",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-indigo-bridge" % tyrianVersion,
        "io.indigoengine" %%% "indigo"            % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-extras"     % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-json-circe" % Dependancies.indigoVersion
      )
    )

lazy val mario =
  (project in file("mario"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "mario")

lazy val nonpm =
  (project in file("no-npm"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "No NPM",
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.NoModule) }
    )

lazy val subcomponents =
  (project in file("subcomponents"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "subcomponents")

lazy val websocket =
  (project in file("websocket"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "websocket")

lazy val zio =
  (project in file("zio"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      version      := tyrianVersion,
      scalaVersion := scala3Version,
      organization := "io.indigoengine",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-zio" % tyrianVersion
      ),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      crossScalaVersions := Seq(scala3Version),
      scalafixOnCompile  := true,
      semanticdbEnabled  := true,
      semanticdbVersion  := scalafixSemanticdb.revision,
      autoAPIMappings    := true
    )
    .settings(
      name := "zio",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      libraryDependencies ++= Seq(
        "dev.zio" %%% "zio"              % "2.0.2",
        "dev.zio" %%% "zio-interop-cats" % "3.3.0+9-bd953aa9-SNAPSHOT"
      )
    )

lazy val exampleProjects: List[String] =
  List(
    "bootstrap",
    "bundler",
    "clock",
    "counter",
    "electron",
    "field",
    "fs2",
    "http",
    "http4sdom",
    "indigo",
    "mario",
    "nonpm",
    "subcomponents",
    "websocket",
    "zio"
  )

lazy val tyrianExamplesProject =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      code := {
        val command = Seq("code", ".")
        val run = sys.props("os.name").toLowerCase match {
          case x if x contains "windows" => Seq("cmd", "/C") ++ command
          case _                         => command
        }
        run.!
      },
      name := "TyrianExamples"
    )
    .settings(
      logo := s"Tyrian Examples (v${version.value})",
      usefulTasks := Seq(
        UsefulTask("", "buildExamples", "Cleans and builds all examples"),
        UsefulTask("", "cleanAll", "Cleans all examples"),
        UsefulTask("", "compileAll", "Compiles all examples"),
        UsefulTask("", "testAll", "Tests all examples"),
        UsefulTask("", "fastOptAll", "Compiles all examples to JS"),
        UsefulTask("", "code", "Launch VSCode")
      ) ++ makeCmds(exampleProjects),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )

lazy val code =
  taskKey[Unit]("Launch VSCode in the current directory")

def makeCmds(names: List[String]): Seq[UsefulTask] =
  names.zipWithIndex.map { case (n, i) =>
    val cmd =
      n match {
        case "bundler" =>
          List(
            s"$n/clean",
            s"$n/fastOptJS::webpack"
          ).mkString(";", ";", "")

        case _ =>
          List(
            s"$n/clean",
            s"$n/fastOptJS"
          ).mkString(";", ";", "")
      }

    UsefulTask("build" + (i + 1), cmd, n)
  }.toSeq

// Top level commands
def applyCommand(projects: List[String], command: String): String =
  projects.map(p => p + "/" + command).mkString(";", ";", "")

def applyToAll(command: String): String =
  List(
    applyCommand(exampleProjects, command)
  ).mkString

addCommandAlias(
  "cleanAll",
  applyToAll("clean")
)
addCommandAlias(
  "compileAll",
  applyToAll("compile")
)
addCommandAlias(
  "testAll",
  applyToAll("test")
)
addCommandAlias(
  "fastOptAll",
  applyToAll("fastOptJS")
)
addCommandAlias(
  "buildExamples",
  List(
    "cleanAll",
    "compileAll",
    "fastOptAll"
  ).mkString(";", ";", "")
)
