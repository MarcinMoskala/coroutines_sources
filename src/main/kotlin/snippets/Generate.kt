import java.io.File

sealed class Snippet
class ExecutableSnippet(val number: Int, val code: String) : Snippet()
class ExampleCode(val code: String) : Snippet()

fun main() {
    File("manuscript")
        .listFiles()!!
        .filter { it.isFile }
        .filter { it.name !in listOf("Book.txt", "302_select.md") }
        .forEach { file ->
            val snippets: List<Snippet> = Regex("```(kotlin|java)\\n([\\s\\S]*?)```")
                .findAll(file.readText())
                .map { it.groupValues[2] }
                .fold(listOf<Snippet>() to 1) { (snippets, nextNum), code ->
                    if (code.startsWith("import")) {
                        (snippets + ExecutableSnippet(nextNum, code)) to nextNum + 1
                    } else {
                        (snippets + ExampleCode(code)) to nextNum
                    }
                }
                .first
                .toList()

            if (snippets.isEmpty()) return@forEach

            File(file.name)
                .also { it.createNewFile() }
                .writeText(snippets.joinToString(separator = "\n\n\n") {
                    when (it) {
                        is ExampleCode -> "```\n${it.code}```"
                        is ExecutableSnippet -> "```\n//${it.number}\n${it.code}```"
                        else -> error("Kotlin error")
                    }
                })

            // Create files for executable snippets
            val executablesFolderName = file.name.replace(".md", "")
            val executablesFolderPath = "src/main/kotlin/snippets/$executablesFolderName"
            snippets.filterIsInstance<ExecutableSnippet>()
                .also { if (it.isNotEmpty()) File(executablesFolderPath).mkdir() }
                .forEach { File("$executablesFolderPath/${it.number}.kt")
                    .writeText("package f_$executablesFolderName.s_${it.number}\n\n${it.code}") }
        }
}
