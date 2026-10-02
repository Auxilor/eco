package com.willfp.eco.util

import com.willfp.eco.core.Eco
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import net.kyori.adventure.text.minimessage.MiniMessage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RichFormatParityTests {
    @BeforeAll
    fun mockEco() {
        val eco = mockk<Eco>(relaxed = true)
        every { eco.formatMiniMessage(any()) } answers {
            val message = firstArg<String>().replace('§', '&')
            runCatching { StringUtils.toLegacy(MiniMessage.miniMessage().deserialize(message)) }.getOrDefault(message)
        }

        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
    }

    @AfterAll
    fun unmockEco() {
        unmockkStatic(Eco::class)
    }

    fun corpus(): List<String> =
        javaClass.getResourceAsStream("/format-corpus.txt")!!
            .bufferedReader()
            .readLines()
            .filter { it.isNotBlank() }

    @ParameterizedTest
    @MethodSource("corpus")
    fun `rich formatting renders the same as legacy formatting`(line: String) {
        assertEquals(
            StringUtils.toLegacy(StringUtils.toComponent(StringUtils.format(line, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS))),
            StringUtils.toLegacy(StringUtils.formatToRichComponent(line))
        )
    }
}
