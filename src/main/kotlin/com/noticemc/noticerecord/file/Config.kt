package com.noticemc.noticerecord.file

import com.noticemc.noticerecord.NoticeRecord.Companion.plugin
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Config {
    lateinit var config: ConfigData

    fun load() {
        val file = plugin.dataFolder.resolve("config.json")
        val json = Json{
            isLenient = true
            prettyPrint = true
        }

        val database = DatabaseConfig("NoticeRecord", 3306, "root", "pass", "localhost")
        val configData = json.encodeToString(ConfigData(database))

        if(!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(configData)
        }
        config = json.decodeFromString(file.readText())
    }
}

@Serializable
data class ConfigData(
    val database: DatabaseConfig,
)

@Serializable
data class DatabaseConfig(
    val name : String,
    val port : Int,
    val user : String,
    val password : String,
    val host : String,
)