package com.dev.unlost.util

import javax.inject.Inject
import javax.inject.Singleton
import java.util.Locale

data class ParsedVoiceResult(
    val title: String,
    val location: String,
    val originalText: String
)

@Singleton
class VietnameseTextParser @Inject constructor() {

    fun parse(text: String): ParsedVoiceResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ParsedVoiceResult("", "", "")
        }

        val lower = trimmed.lowercase(Locale.getDefault())

        val locationKeywords = listOf(" ở ", " tại ", " trong ", " trên ", " dưới ", " gần ")

        for (keyword in locationKeywords) {
            val index = lower.indexOf(keyword)
            if (index != -1) {
                var rawTitle = trimmed.substring(0, index).trim()
                var rawLocation = trimmed.substring(index + keyword.length).trim()

                val prefixes = listOf("tôi để ", "tôi cất ", "tôi làm mất ", "để ", "cất ", "mất ", "lưu ")
                for (prefix in prefixes) {
                    if (rawTitle.lowercase(Locale.getDefault()).startsWith(prefix)) {
                        rawTitle = rawTitle.substring(prefix.length).trim()
                    }
                }

                return ParsedVoiceResult(
                    title = capitalizeFirstLetter(rawTitle.ifEmpty { trimmed }),
                    location = capitalizeFirstLetter(rawLocation),
                    originalText = trimmed
                )
            }
        }

        var fallbackTitle = trimmed
        val prefixes = listOf("tôi để ", "tôi cất ", "tôi làm mất ", "để ", "cất ", "mất ")
        for (prefix in prefixes) {
            if (fallbackTitle.lowercase(Locale.getDefault()).startsWith(prefix)) {
                fallbackTitle = fallbackTitle.substring(prefix.length).trim()
            }
        }

        return ParsedVoiceResult(
            title = capitalizeFirstLetter(fallbackTitle),
            location = "",
            originalText = trimmed
        )
    }

    private fun capitalizeFirstLetter(input: String): String {
        if (input.isEmpty()) return ""
        return input.substring(0, 1).uppercase(Locale.getDefault()) + input.substring(1)
    }
}
