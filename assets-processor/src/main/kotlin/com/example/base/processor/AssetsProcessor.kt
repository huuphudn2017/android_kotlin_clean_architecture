package com.example.base.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File

/**
 * Generates two objects in `<appPackage>.generated`:
 *
 * - `Assets` — one nested object per folder under `assetsDir`, one `const val` URI per file
 *   (`file:///android_asset/...`, usable directly by Coil or a WebView).
 * - `RawRes` — one `val` per file under `res/raw*` pointing to `R.raw.<name>`.
 *
 * Options (set in `app/build.gradle.kts` → `ksp { arg(...) }`):
 * `appPackage` (required), `assetsDir`, `resDir`.
 *
 * Both objects are always generated (possibly empty) so imports never break when a folder is empty.
 */
class AssetsProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {

    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()
        generated = true

        val appPackage = options["appPackage"] ?: run {
            logger.error("AssetsProcessor: missing ksp arg 'appPackage'")
            return emptyList()
        }
        val pkg = "$appPackage.generated"

        write(pkg, "Assets", AssetsSource.assets(pkg, options["assetsDir"]?.let(::File)))
        write(pkg, "RawRes", AssetsSource.raw(pkg, appPackage, options["resDir"]?.let(::File)))
        return emptyList()
    }

    private fun write(pkg: String, name: String, source: String) {
        codeGenerator.createNewFile(Dependencies.ALL_FILES, pkg, name).use {
            it.write(source.toByteArray())
        }
    }
}

/** Pure source builders, kept separate from KSP so they can be unit tested. */
internal object AssetsSource {

    private val ASSET_EXTENSIONS = setOf(
        "png", "jpg", "jpeg", "webp", "gif", "svg", "json", "txt", "html", "mp3", "mp4", "ttf", "otf",
    )

    fun assets(pkg: String, assetsDir: File?): String = buildString {
        appendLine("package $pkg")
        appendLine()
        appendLine("object Assets {")
        if (assetsDir != null && assetsDir.isDirectory) appendFolder(assetsDir, assetsDir, "    ")
        appendLine("}")
    }

    fun raw(pkg: String, appPackage: String, resDir: File?): String = buildString {
        val files = resDir?.listFiles()
            ?.filter { it.isDirectory && (it.name == "raw" || it.name.startsWith("raw-")) }
            ?.flatMap { dir -> dir.listFiles()?.filter { it.isFile && !it.isHidden }.orEmpty() }
            ?.map { it.nameWithoutExtension }
            ?.distinct()
            ?.sorted()
            .orEmpty()

        appendLine("package $pkg")
        appendLine()
        if (files.isNotEmpty()) {
            appendLine("import $appPackage.R")
            appendLine()
        }
        appendLine("object RawRes {")
        files.forEach { appendLine("    val ${it.toIdentifier()}: Int get() = R.raw.$it") }
        appendLine("}")
    }

    private fun StringBuilder.appendFolder(folder: File, root: File, indent: String) {
        val children = folder.listFiles()?.filterNot { it.isHidden }?.sortedBy { it.name }.orEmpty()
        val used = mutableSetOf<String>()

        children.filter { it.isDirectory }.forEach { dir ->
            val name = uniqueName(dir.name.toIdentifier().replaceFirstChar(Char::uppercase), used)
            appendLine("${indent}object $name {")
            appendFolder(dir, root, "$indent    ")
            appendLine("$indent}")
        }

        children.filter { it.isFile && it.extension.lowercase() in ASSET_EXTENSIONS }.forEach { file ->
            val name = uniqueName(file.nameWithoutExtension.toIdentifier(), used)
            val path = file.relativeTo(root).invariantSeparatorsPath
            appendLine("${indent}const val $name = \"file:///android_asset/$path\"")
        }
    }

    private fun uniqueName(base: String, used: MutableSet<String>): String {
        var name = base
        var i = 2
        while (!used.add(name)) name = "$base${i++}"
        return name
    }
}

/** `my_icon-2 final` → `myIcon2Final`; prefixed with `_` when it would start with a digit. */
internal fun String.toIdentifier(): String {
    val camel = split('_', '-', ' ', '.')
        .filter { it.isNotBlank() }
        .joinToString("") { part -> part.filter(Char::isLetterOrDigit).replaceFirstChar(Char::uppercase) }
        .replaceFirstChar(Char::lowercase)
    return when {
        camel.isEmpty() -> "_unnamed"
        camel.first().isDigit() -> "_$camel"
        else -> camel
    }
}
