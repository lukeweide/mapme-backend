package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object VisitedCells : Table("visitedcells") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val s2CellId = long("s2_cell_id")

    // Stats
    val photoCount = integer("photo_count").default(0)
    val firstVisitedAt = timestamp("first_visited_at")
    val lastVisitedAt = timestamp("last_visited_at")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
        index(false, s2CellId)
        uniqueIndex(userId, s2CellId)  // One cell per user
    }
}