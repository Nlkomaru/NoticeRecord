package com.noticemc.noticerecord.commands

import cloud.commandframework.annotations.*
import com.noticemc.noticerecord.NoticeRecord.Companion.plugin
import com.noticemc.noticerecord.coroutines.minecraft
import com.noticemc.noticerecord.database.RecordData
import com.noticemc.noticerecord.database.RecordData.player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class RecordCommand {

    @CommandMethod("nr record <radius>")
    @CommandDescription("Record a data")
    @CommandPermission("noticerecord.command.record")
    suspend fun record(sender: CommandSender, @Argument(value = "radius") radius: Int) {
        if (sender !is BlockCommandSender) {
            return
        }
        lateinit var list: ArrayList<UUID>
        withContext(Dispatchers.minecraft) {
            val block = sender.block
            val location = block.location
            list = location.getNearbyPlayers(radius.toDouble()).map { it.uniqueId } as ArrayList<UUID>
        }
        val recordedList = arrayListOf<UUID>()

        transaction {
            RecordData.selectAll().forEach {
                recordedList.add(it[player].toUUID())
            }
        }
        recordedList.forEach {
            list.remove(it)
        }

        list.forEach { player ->
            transaction {
                RecordData.insert {
                    it[RecordData.player] = player.toString()
                    it[timestamp] = LocalDateTime.now()
                    it[recieved] = false
                }
                plugin.logger.info("Recorded $player")
            }
        }
    }

    private fun String.toUUID(): UUID {
        return UUID.fromString(this)
    }
}