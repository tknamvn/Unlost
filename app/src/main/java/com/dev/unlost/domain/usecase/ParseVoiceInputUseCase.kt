package com.dev.unlost.domain.usecase

import com.dev.unlost.util.ParsedVoiceResult
import com.dev.unlost.util.VietnameseTextParser
import javax.inject.Inject

class ParseVoiceInputUseCase @Inject constructor(
    private val parser: VietnameseTextParser
) {
    operator fun invoke(spokenText: String): ParsedVoiceResult {
        return parser.parse(spokenText)
    }
}
