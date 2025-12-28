package com.mapme.routes

import com.mapme.data.services.S2Service
import com.mapme.domain.models.*
import data.repositories.VisitedCellsRepository
import domain.models.GridGeometry
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.visitedCellsRoutes(
    visitedCellsRepository: VisitedCellsRepository,
    s2Service: S2Service
) {

    route("/api/v1/users/me") {

        // GET /api/v1/users/me/visited-cells - Get visited cells as GeoJSON
        get("/visited-cells") {
            val userId = UUID.fromString("d8f9959c-0ab8-44aa-ad10-f03ae2764fde")

            // Optional viewport filter
            val minLat = call.request.queryParameters["minLat"]?.toDoubleOrNull()
            val maxLat = call.request.queryParameters["maxLat"]?.toDoubleOrNull()
            val minLon = call.request.queryParameters["minLon"]?.toDoubleOrNull()
            val maxLon = call.request.queryParameters["maxLon"]?.toDoubleOrNull()

            val visitedCells = if (minLat != null && maxLat != null && minLon != null && maxLon != null) {
                // Get cells in viewport
                val cellIds = s2Service.getCellsInViewport(minLat, maxLat, minLon, maxLon)
                visitedCellsRepository.findByUserIdInViewport(userId, cellIds)
            } else {
                // Get all visited cells
                visitedCellsRepository.findByUserId(userId)
            }

            val features = visitedCells.map { cell ->
                val boundary = s2Service.cellToBoundary(cell.s2CellId)

                // GeoJSON Polygon
                val coordinates = listOf(
                    boundary.map { (lat, lon) -> listOf(lon, lat) } +
                            listOf(boundary.first().let { (lat, lon) -> listOf(lon, lat) })
                )

                VisitedCellFeature(
                    geometry = GridGeometry(coordinates = coordinates),
                    properties = VisitedCellProperties(
                        cellId = cell.s2CellId,
                        photoCount = cell.photoCount,
                        firstVisitedAt = cell.firstVisitedAt.toEpochMilliseconds(),
                        lastVisitedAt = cell.lastVisitedAt.toEpochMilliseconds()
                    )
                )
            }

            call.respond(VisitedCellsResponse(features = features))
        }

        // GET /api/v1/users/me/heatmap - Alias for visited-cells (backward compatibility)
        get("/heatmap") {
            val userId = UUID.fromString("d8f9959c-0ab8-44aa-ad10-f03ae2764fde")

            val minLat = call.request.queryParameters["minLat"]?.toDoubleOrNull()
            val maxLat = call.request.queryParameters["maxLat"]?.toDoubleOrNull()
            val minLon = call.request.queryParameters["minLon"]?.toDoubleOrNull()
            val maxLon = call.request.queryParameters["maxLon"]?.toDoubleOrNull()

            val visitedCells = if (minLat != null && maxLat != null && minLon != null && maxLon != null) {
                val cellIds = s2Service.getCellsInViewport(minLat, maxLat, minLon, maxLon)
                visitedCellsRepository.findByUserIdInViewport(userId, cellIds)
            } else {
                visitedCellsRepository.findByUserId(userId)
            }

            val features = visitedCells.map { cell ->
                val boundary = s2Service.cellToBoundary(cell.s2CellId)

                val coordinates = listOf(
                    boundary.map { (lat, lon) -> listOf(lon, lat) } +
                            listOf(boundary.first().let { (lat, lon) -> listOf(lon, lat) })
                )

                VisitedCellFeature(
                    geometry = GridGeometry(coordinates = coordinates),
                    properties = VisitedCellProperties(
                        cellId = cell.s2CellId,
                        photoCount = cell.photoCount,
                        firstVisitedAt = cell.firstVisitedAt.toEpochMilliseconds(),
                        lastVisitedAt = cell.lastVisitedAt.toEpochMilliseconds()
                    )
                )
            }

            call.respond(VisitedCellsResponse(features = features))
        }
    }
}