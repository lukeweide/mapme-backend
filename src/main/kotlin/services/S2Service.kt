package com.mapme.data.services

import com.google.common.geometry.S2CellId
import com.google.common.geometry.S2LatLng
import com.mapme.config.AppConfig

class S2Service {

    /**
     * Converts GPS coordinates to S2 Cell ID
     */
    fun coordinatesToCell(
        latitude: Double,
        longitude: Double,
        level: Int = AppConfig.S2.DEFAULT_CELL_LEVEL
    ): Long {
        val latLng = S2LatLng.fromDegrees(latitude, longitude)
        val cellId = S2CellId.fromLatLng(latLng)
        return cellId.parent(level).id()
    }

    /**
     * Returns the 4 corner points of an S2 cell
     */
    fun cellToBoundary(cellId: Long): List<Pair<Double, Double>> {
        val cell = com.google.common.geometry.S2Cell(S2CellId(cellId))

        return (0 until 4).map { i ->
            val vertex = cell.getVertex(i)
            val latLng = S2LatLng(vertex)
            Pair(latLng.latDegrees(), latLng.lngDegrees())
        }
    }

    /**
     * Returns the center point of an S2 cell
     */
    fun cellToCenter(cellId: Long): Pair<Double, Double> {
        val cell = S2CellId(cellId)
        val latLng = S2LatLng(cell.toPoint())
        return Pair(latLng.latDegrees(), latLng.lngDegrees())
    }
}