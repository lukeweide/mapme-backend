package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.Table

object Cities : Table("cities") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val country = varchar("country", 255)

    // GPS Coordinates
    val latitude = double("latitude")
    val longitude = double("longitude")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, name)
        index(false, country)
    }
}