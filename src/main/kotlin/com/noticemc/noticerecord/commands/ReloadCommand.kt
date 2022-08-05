package com.noticemc.noticerecord.commands

import cloud.commandframework.annotations.*
import com.noticemc.noticerecord.NoticeRecord
import com.noticemc.noticerecord.file.Config
import org.bukkit.command.CommandSender

class ReloadCommand {

    @CommandMethod("nr reload")
    @CommandPermission("noticerecord.command.reload")
    @CommandDescription("Reloads the config")
    fun reload(sender: CommandSender) {
        Config.load()
        NoticeRecord.plugin.setupDatabase()
        sender.sendMessage("Successfully reloaded the config and database")
    }
}