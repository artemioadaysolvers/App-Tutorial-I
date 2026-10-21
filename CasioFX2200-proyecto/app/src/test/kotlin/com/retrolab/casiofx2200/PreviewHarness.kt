package com.retrolab.casiofx2200

/** Test-only transport for exercising the production Kotlin engine from a local UI.
 * This file is compiled into JVM test classes and is never packaged in the APK. */
object PreviewHarness {
    private fun quote(s: String) = "\""+s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n")+"\""
    @JvmStatic fun main(args: Array<String>) {
        val engine=CalculatorEngine()
        generateSequence(::readLine).forEach { command ->
            try {
                val parts=command.split(' ',limit=2)
                when(parts[0]) { "KEY" -> engine.press(parts[1]); "MODE" -> engine.setMode(parts[1]) }
                val values=linkedMapOf(
                    "display" to quote(engine.display()),"value" to engine.value.toString(),
                    "memory" to quote(engine.format(engine.memory)),"mode" to quote(engine.mode),
                    "inverse" to engine.inverse.toString(),"hyper" to engine.hyper.toString(),
                    "powered" to engine.powered.toString(),"pending" to quote(engine.pendingDisplay()),
                    "count" to engine.count.toString(),"error" to quote(engine.error),"notice" to quote(engine.notice)
                )
                println(values.entries.joinToString(",","{","}") { quote(it.key)+":"+it.value })
            } catch(e: Exception) { println("{\"failure\":"+quote(e.message ?: "Error")+"}") }
        }
    }
}
