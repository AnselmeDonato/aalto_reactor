file://<HOME>/Library/Mobile%20Documents/com~apple~CloudDocs/Desktop/Aalto/Concurrent%20Programming/reactor/build.sbt
### scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Predef

occurred in the presentation compiler.

action parameters:
uri: file://<HOME>/Library/Mobile%20Documents/com~apple~CloudDocs/Desktop/Aalto/Concurrent%20Programming/reactor/build.sbt
text:
```scala
import _root_.scala.xml.{TopScope=>$scope}
import _root_.sbt._
import _root_.sbt.Keys._
import _root_.sbt.nio.Keys._
import _root_.sbt.ScriptedPlugin.autoImport._, _root_.sbt.plugins.JUnitXmlReportPlugin.autoImport._, _root_.sbt.plugins.MiniDependencyTreePlugin.autoImport._, _root_.bloop.integrations.sbt.BloopPlugin.autoImport._
import _root_.sbt.plugins.IvyPlugin, _root_.sbt.plugins.JvmPlugin, _root_.sbt.plugins.CorePlugin, _root_.sbt.ScriptedPlugin, _root_.sbt.plugins.SbtPlugin, _root_.sbt.plugins.SemanticdbPlugin, _root_.sbt.plugins.JUnitXmlReportPlugin, _root_.sbt.plugins.Giter8TemplatePlugin, _root_.sbt.plugins.MiniDependencyTreePlugin, _root_.bloop.integrations.sbt.BloopPlugin
name := "CS-E4110-assignment"

version := "22.11.01"

scalaVersion := "2.13.6"

libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.9"

// Define the main method
// mainClass in (Compile, run) := Some("hangman.HangmanGame")
mainClass in (Compile, run) := Some("Hello")

```



#### Error stacktrace:

```

```
#### Short summary: 

scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Predef