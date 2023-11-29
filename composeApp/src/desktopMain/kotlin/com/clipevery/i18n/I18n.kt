package com.clipevery.i18n

import com.clipevery.config.ConfigManager
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

val logger = KotlinLogging.logger {}

const val en = "en"

val languageList = listOf(en, "es", "jp", "zh")

val languageMap = ConcurrentHashMap<String, Copywriter>()

fun getText(id: String): String {
    return languageMap.computeIfAbsent(en) { CopywriterImpl(en) }.getText(id)
}

open class GlobalCopywriterImpl(val configManager: ConfigManager): GlobalCopywriter {

    private var copywriter: Copywriter = languageMap
        .computeIfAbsent(configManager.config.language) {
        CopywriterImpl(configManager.config.language)
    }

    override fun switchLanguage(language: String) {
        copywriter = languageMap.computeIfAbsent(language) { CopywriterImpl(language) }
        configManager.updateConfig { it.copy(language = language) }
    }

    override fun getAllLanguages(): List<Language> {
        return languageList
            .map { it ->
                val copywriter = languageMap.computeIfAbsent(it) { CopywriterImpl(it) }
                val abridge = copywriter.getAbridge()
                val name = copywriter.getText("CurrentLanguage")
                Language(abridge, name)
            }
    }

    override fun getText(id: String): String {
        return copywriter.getText(id)
    }

    override fun getAbridge(): String {
        return copywriter.getAbridge()
    }
}


class CopywriterImpl(private val language: String) : Copywriter {

    private val properties: Properties = loadProperties()

    private var currentLanguage: String = en


    private fun load(properties: Properties, language: String) {
        properties.load(CopywriterImpl::class.java.getResourceAsStream("/i18n/${language}.properties")
            ?.let {
                InputStreamReader(
                    it,
                    StandardCharsets.UTF_8
                )
            } ?: (throw FileNotFoundException("No properties for $language")) )
    }

    private fun loadProperties(): Properties {
        val properties = Properties()
        currentLanguage = try {
            load(properties, language)
            language
        } catch (e: Exception) {
            logger.error(e) { "Error loading $language properties: ${e.message}" }
            load(properties, en)
            en
        }
        return properties
    }

    override fun getText(id: String): String {
        val value: String? = properties.getProperty(id)
        return if (value == null) {
            logger.error { "No value for $id" }
            "null"
        } else {
            value
        }
    }

    override fun getAbridge(): String {
        return language
    }


}