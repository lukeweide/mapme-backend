package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlinx.datetime.Clock

object Users : Table("users") {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", 255)
    val email = varchar("email", 255).uniqueIndex()
    val createdAt = timestamp("created_at").clientDefault { Clock.System.now() }

    override val primaryKey = PrimaryKey(id)
}