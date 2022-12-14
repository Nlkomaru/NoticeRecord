package com.noticemc.noticerecord

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import com.noticemc.noticerecord.commands.RecordCommand
import com.noticemc.noticerecord.commands.ReloadCommand
import com.noticemc.noticerecord.database.RecordData
import com.noticemc.noticerecord.file.Config
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class NoticeRecord : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        Config.load()
        setupDatabase()
        setupCommand()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun setupDatabase() {
        Database.connect(url = "jdbc:mysql://${Config.config.database.host}:${Config.config.database.port}/${Config.config.database.name}",
            driver = "com.mysql.jdbc.Driver",
            user = Config.config.database.user,
            password = Config.config.database.password)

        transaction {
            SchemaUtils.create(RecordData)
        }
    }

    private fun setupCommand() {

        val commandManager: PaperCommandManager<CommandSender> = PaperCommandManager(this,
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build(),
            java.util.function.Function.identity(),
            java.util.function.Function.identity())

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java) {
            SimpleCommandMeta.empty()
        }.installCoroutineSupport()

        annotationParser.parse(ReloadCommand())
        annotationParser.parse(RecordCommand())

    }

    companion object {
        lateinit var plugin: NoticeRecord
            private set
    }
}