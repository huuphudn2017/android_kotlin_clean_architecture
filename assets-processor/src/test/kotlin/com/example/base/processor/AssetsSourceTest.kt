package com.example.base.processor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AssetsSourceTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `identifier is camel case and never starts with a digit`() {
        assertEquals("myIconFinal", "my_icon-final".toIdentifier())
        assertEquals("_2x", "2x".toIdentifier())
        assertEquals("_unnamed", "__".toIdentifier())
    }

    @Test
    fun `assets become nested objects with asset uris`() {
        val root = tmp.newFolder("assets")
        root.resolve("images").mkdirs()
        root.resolve("images/empty_state.png").writeText("x")
        root.resolve("data.json").writeText("{}")
        root.resolve("notes.unknown").writeText("ignored")

        val source = AssetsSource.assets("app.generated", root)

        assertTrue(source.contains("object Images {"))
        assertTrue(source.contains("const val emptyState = \"file:///android_asset/images/empty_state.png\""))
        assertTrue(source.contains("const val data = \"file:///android_asset/data.json\""))
        assertTrue(!source.contains("notes"))
    }

    @Test
    fun `duplicate names get a numeric suffix`() {
        val root = tmp.newFolder("assets")
        root.resolve("logo.png").writeText("x")
        root.resolve("logo.svg").writeText("x")

        val source = AssetsSource.assets("app.generated", root)

        assertTrue(source.contains("const val logo = "))
        assertTrue(source.contains("const val logo2 = "))
    }

    @Test
    fun `missing folders still produce empty objects`() {
        assertTrue(AssetsSource.assets("app.generated", null).contains("object Assets {"))
        val raw = AssetsSource.raw("app.generated", "app", null)
        assertTrue(raw.contains("object RawRes {"))
        assertTrue("no R import when there is nothing to reference", !raw.contains("import app.R"))
    }

    @Test
    fun `raw files across qualifiers are listed once`() {
        val res = tmp.newFolder("res")
        res.resolve("raw").mkdirs()
        res.resolve("raw-vi").mkdirs()
        res.resolve("raw/ding.mp3").writeText("x")
        res.resolve("raw-vi/ding.mp3").writeText("x")

        val source = AssetsSource.raw("app.generated", "app", res)

        assertEquals(1, Regex("val ding:").findAll(source).count())
        assertTrue(source.contains("R.raw.ding"))
    }
}
