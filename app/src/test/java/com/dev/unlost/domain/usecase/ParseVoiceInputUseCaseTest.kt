package com.dev.unlost.domain.usecase

import com.dev.unlost.util.VietnameseTextParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ParseVoiceInputUseCaseTest {

    private lateinit var parseVoiceInputUseCase: ParseVoiceInputUseCase
    private lateinit var parser: VietnameseTextParser

    @Before
    fun setUp() {
        parser = VietnameseTextParser()
        parseVoiceInputUseCase = ParseVoiceInputUseCase(parser)
    }

    @Test
    fun invoke_validSpokenText_delegatesToParserAndReturnsResult() {
        val result = parseVoiceInputUseCase("Tôi để chìa khóa ở bàn làm việc")
        assertEquals("Chìa khóa", result.title)
        assertEquals("Bàn làm việc", result.location)
    }

    @Test
    fun invoke_emptySpokenText_returnsEmptyResult() {
        val result = parseVoiceInputUseCase("")
        assertEquals("", result.title)
        assertEquals("", result.location)
    }
}
