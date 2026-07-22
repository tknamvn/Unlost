package com.dev.unlost.util

import javax.inject.Inject
import javax.inject.Singleton

data class ParsedVoiceResult(
    val title: String,
    val location: String,
    val originalText: String
)

@Singleton
class VietnameseTextParser @Inject constructor() {

    /**
     * Bóc tách tên đồ vật & vị trí từ chuỗi nói tiếng Việt.
     * Ví dụ: "Tôi để chìa khóa ở trong tủ lạnh" -> title = "chìa khóa", location = "trong tủ lạnh"
     */
    fun parse(text: String): ParsedVoiceResult {
        val trimmed = text.trim()
        val lower = trimmed.lowercase()

        val locationKeywords = listOf(" ở ", " tại ", " trong ", " trên ", " dưới ", " gần ")

        for (keyword in locationKeywords) {
            val index = lower.indexOf(keyword)
            if (index != -1) {
                var rawTitle = trimmed.substring(0, index).trim()
                val rawLocation = trimmed.substring(index + keyword.length).trim()

                val prefixToRemove = listOf("tôi để ", "để ", "tôi cất ", "cất ", "tôi làm mất ", "mất ")
                for (prefix in prefixToRemove) {
                    if (rawTitle.lowercase().startsWith(prefix)) {
                        rawTitle = rawTitle.substring(prefix.length).trim()
                    }
                }

                return ParsedVoiceResult(
                    title = rawTitle.ifEmpty { trimmed },
                    location = rawLocation,
                    originalText = trimmed
                )
            }
        }

        return ParsedVoiceResult(
            title = trimmed,
            location = "",
            originalText = trimmed
        )
    }
}
