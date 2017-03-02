ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

parallelExecution in Test := false

assemblyJarName in assembly := "scala-etl.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

test in assembly := {}

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

runMain in Compile <<= Defaults.runMainTask(fullClasspath in Compile, runner in (Compile, run))

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}
