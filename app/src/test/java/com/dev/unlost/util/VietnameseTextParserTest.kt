package com.dev.unlost.util

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VietnameseTextParserTest {

    private lateinit var parser: VietnameseTextParser

    @Before
    fun setUp() {
        parser = VietnameseTextParser()
    }

    @Test
    fun parse_standardSentence_extractsTitleAndLocation() {
        val result = parser.parse("Tôi để chìa khóa ở trong ngăn kéo")
        assertEquals("Chìa khóa", result.title)
        assertEquals("Trong ngăn kéo", result.location)
    }

    @Test
    fun parse_wordEndingWithO_doesNotSplitInsideWord() {
        val result = parser.parse("Tôi cất quần áo ở trong tủ")
        assertEquals("Quần áo", result.title)
        assertEquals("Trong tủ", result.location)
    }

    @Test
    fun parse_wordPho_doesNotSplitInsideWord() {
        val result = parser.parse("Tôi để phở ở bàn ăn")
        assertEquals("Phở", result.title)
        assertEquals("Bàn ăn", result.location)
    }

    @Test
    fun parse_invertedSentence_extractsTitleAndLocation() {
        val result = parser.parse("Trong ngăn kéo bàn làm việc có chiếc chìa khóa xe")
        assertEquals("Chiếc chìa khóa xe", result.title)
        assertEquals("Ngăn kéo bàn làm việc", result.location)
    }

    @Test
    fun parse_invertedSentenceWithPrefix_extractsTitleAndLocation() {
        val result = parser.parse("Tôi vừa cất ở trên tủ quần áo có cái ví màu nâu")
        assertEquals("Cái ví màu nâu", result.title)
        assertEquals("Trên tủ quần áo", result.location)
    }

    @Test
    fun parse_compoundLocationKeyword_extractsCorrectly() {
        val result = parser.parse("Chìa khóa xe máy nằm tại tủ giày")
        assertEquals("Chìa khóa xe máy", result.title)
        assertEquals("Tủ giày", result.location)
    }

    @Test
    fun parse_noLocationKeyword_returnsFallbackTitle() {
        val result = parser.parse("Tôi làm mất chiếc đồng hồ thông minh")
        assertEquals("Chiếc đồng hồ thông minh", result.title)
        assertEquals("", result.location)
    }
}
