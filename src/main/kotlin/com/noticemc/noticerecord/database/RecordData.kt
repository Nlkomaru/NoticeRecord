package com.noticemc.noticerecord.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime


object RecordData: Table() {
    val timestamp = datetime("timestamp")
    val player = varchar("player", 40)
    val recieved = bool("recieved")
}