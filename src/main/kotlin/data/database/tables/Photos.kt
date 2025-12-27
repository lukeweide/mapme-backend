package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.ReferenceOption

object Photos : Table("photos") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val filePath = varchar("file_path", 500)
    val thumbnailPath = varchar("thumbnail_path", 500).nullable()

    // GPS Coordinates
    val latitude = double("latitude")
    val longitude = double("longitude")

    // S2 Cell
    val s2CellId = long("s2_cell_id")

    // EXIF Metadata
    val cameraMake = varchar("camera_make", 255).nullable()
    val cameraModel = varchar("camera_model", 255).nullable()
    val takenAt = timestamp("taken_at").nullable()

    // Timestamps
    val uploadedAt = timestamp("uploaded_at").clientDefault { Clock.System.now() }

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
        index(false, s2CellId)
        index(false, takenAt)
    }
}