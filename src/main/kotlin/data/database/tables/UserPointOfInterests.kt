package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.ReferenceOption

object UserPointOfInterests : Table("userpointofinterests") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val poiId = uuid("poi_id").references(PointOfInterests.id, onDelete = ReferenceOption.CASCADE)
    val photoId = uuid("photo_id").references(Photos.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val discoveredAt = timestamp("discovered_at").clientDefault { Clock.System.now() }

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
        index(false, poiId)
        uniqueIndex(userId, poiId)  // One POI discovery per user
    }
}