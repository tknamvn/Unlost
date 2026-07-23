package com.dev.unlost.util

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class ParsedVoiceResult(
    val title: String,
    val location: String,
    val originalText: String
)

@Singleton
class VietnameseTextParser @Inject constructor() {

    // Danh sách tiền tố mở đầu câu nói thông dụng (sắp xếp theo độ dài giảm dần)
    private val prefixes = listOf(
        "tôi vừa làm mất ", "tôi làm mất ", "tôi vừa cất ", "tôi vừa để ", "tôi vừa đặt ",
        "tôi cất ", "tôi để ", "tôi đặt ", "tôi lưu ", "tôi có ",
        "vừa làm mất ", "vừa cất ", "vừa để ", "vừa đặt ",
        "làm mất ", "ghi nhớ ", "nhớ ", "lưu ", "cất ", "để ", "mất ", "đặt "
    )

    // Pattern nhận diện câu đảo ngữ (ví dụ: "Trong ngăn kéo có chìa khóa", "Ở trên bàn có chiếc ví")
    private val invertedPattern = Regex(
        "(?i)^\\s*(?:ở|tại|trong|trên|dưới|gần|bên trong|bên trên|bên dưới|phía trên|phía dưới|nằm ở|nằm tại|nằm trong)\\s+(.+?)\\s+có\\s+(.+)$"
    )

    // Các từ khóa chỉ vị trí độc lập (sắp xếp theo độ dài giảm dần để ưu tiên từ dài)
    private val locationKeywords = listOf(
        "nằm tại", "nằm trong", "nằm trên", "nằm dưới", "nằm gần", "nằm ở",
        "bên trong", "bên trên", "bên dưới", "phía trên", "phía dưới",
        "ở", "tại", "trong", "trên", "dưới", "gần"
    )

    // Regex khớp từ khóa vị trí đứng độc lập (có ranh giới khoảng trắng/đầu câu)
    private val locationRegex = Regex(
        "(?i)(?<=\\s|^)(" + locationKeywords.joinToString("|") { Regex.escape(it) } + ")(?=\\s+)"
    )

    fun parse(text: String): ParsedVoiceResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ParsedVoiceResult("", "", "")
        }

        // 1. Lọc các từ tiền tố mở đầu câu nói
        var cleanedText = trimmed
        var matchFound = true
        while (matchFound) {
            matchFound = false
            for (prefix in prefixes) {
                if (cleanedText.lowercase(Locale.getDefault()).startsWith(prefix)) {
                    cleanedText = cleanedText.substring(prefix.length).trim()
                    matchFound = true
                    break
                }
            }
        }

        if (cleanedText.isEmpty()) {
            cleanedText = trimmed
        }

        // 2. Kiểm tra câu đảo ngữ: "Ở/Trong/Tại [Vị trí] có [Đồ vật]"
        val invertedMatch = invertedPattern.matchEntire(cleanedText)
        if (invertedMatch != null) {
            val rawLocation = invertedMatch.groupValues[1].trim()
            val rawTitle = invertedMatch.groupValues[2].trim()
            if (rawTitle.isNotEmpty() && rawLocation.isNotEmpty()) {
                return ParsedVoiceResult(
                    title = capitalizeFirstLetter(rawTitle),
                    location = capitalizeFirstLetter(rawLocation),
                    originalText = trimmed
                )
            }
        }

        // 3. Kiểm tra câu thuận: "[Đồ vật] [Từ khóa vị trí] [Vị trí]"
        val matchResult = locationRegex.find(cleanedText)
        if (matchResult != null) {
            val keywordIndex = matchResult.range.first
            val keywordLength = matchResult.value.length

            var rawTitle = cleanedText.substring(0, keywordIndex).trim()
            var rawLocation = cleanedText.substring(keywordIndex + keywordLength).trim()

            // Loại bỏ động từ hành động ở cuối tên đồ vật nếu có (vd: "Quần áo cất ở tủ" -> rawTitle: "Quần áo cất" -> "Quần áo")
            val trailingVerbs = listOf("để", "cất", "đặt", "lưu", "xếp")
            for (verb in trailingVerbs) {
                if (rawTitle.lowercase(Locale.getDefault()).endsWith(" $verb")) {
                    rawTitle = rawTitle.substring(0, rawTitle.length - verb.length - 1).trim()
                }
            }

            if (rawTitle.isNotEmpty() && rawLocation.isNotEmpty()) {
                return ParsedVoiceResult(
                    title = capitalizeFirstLetter(rawTitle),
                    location = capitalizeFirstLetter(rawLocation),
                    originalText = trimmed
                )
            }
        }

        // 4. Fallback: Nếu không tìm thấy từ chỉ vị trí, lấy toàn bộ văn bản đã lọc làm tiêu đề
        return ParsedVoiceResult(
            title = capitalizeFirstLetter(cleanedText),
            location = "",
            originalText = trimmed
        )
    }

    private fun capitalizeFirstLetter(input: String): String {
        if (input.isEmpty()) return ""
        return input.substring(0, 1).uppercase(Locale.getDefault()) + input.substring(1)
    }
}
