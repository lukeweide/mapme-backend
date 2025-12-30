package com.mapme.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object PointOfInterests : Table("pointofinterests") {
    val id = uuid("id").autoGenerate()
    val osmId = long("osm_id").nullable()
    val name = varchar("name", 255)
    val type = varchar("type", 100)
    val cityId = uuid("city_id").references(Cities.id, onDelete = ReferenceOption.SET_NULL).nullable()

    // GPS Coordinates
    val latitude = double("latitude")
    val longitude = double("longitude")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, cityId)
        index(false, type)
        index(false, osmId)
    }
}