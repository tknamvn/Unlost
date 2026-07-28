package com.dev.unlost.ui.widget

import com.dev.unlost.domain.repository.ItemRepository
import com.dev.unlost.util.VietnameseTextParser
import com.dev.unlost.util.VoiceSettingsManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun itemRepository(): ItemRepository
    fun vietnameseTextParser(): VietnameseTextParser
    fun voiceSettingsManager(): VoiceSettingsManager
}
