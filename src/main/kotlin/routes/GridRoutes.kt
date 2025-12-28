package routes

import com.mapme.config.AppConfig
import com.mapme.data.services.S2Service
import domain.models.CellDetailsResponse
import domain.models.CenterPoint
import domain.models.GridFeature
import domain.models.GridGeometry
import domain.models.GridProperties
import domain.models.GridTilesResponse
import domain.models.Point
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gridRoutes(s2Service: S2Service) {

    route("/api/v1/grid") {

        // GET /api/v1/grid/tiles - Get grid cells in viewport
        get("/tiles") {
            val minLat = call.request.queryParameters["minLat"]?.toDoubleOrNull()
            val maxLat = call.request.queryParameters["maxLat"]?.toDoubleOrNull()
            val minLon = call.request.queryParameters["minLon"]?.toDoubleOrNull()
            val maxLon = call.request.queryParameters["maxLon"]?.toDoubleOrNull()
            val level = call.request.queryParameters["level"]?.toIntOrNull() ?: AppConfig.S2.CELL_LEVEL

            if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing bounds parameters"))
                return@get
            }

            val cellIds = s2Service.getCellsInViewport(minLat, maxLat, minLon, maxLon, level)

            val features = cellIds.map { cellId ->
                val boundary = s2Service.cellToBoundary(cellId)

                // GeoJSON Polygon: [[[lon, lat], [lon, lat], ...]]
                val coordinates = listOf(
                    boundary.map { (lat, lon) -> listOf(lon, lat) } +
                            listOf(boundary.first().let { (lat, lon) -> listOf(lon, lat) })  // Close polygon
                )

                GridFeature(
                    geometry = GridGeometry(coordinates = coordinates),
                    properties = GridProperties(cellId = cellId)
                )
            }

            call.respond(GridTilesResponse(features = features))
        }

        // GET /api/v1/grid/cells/{cellId} - Get cell details
        get("/cells/{cellId}") {
            val cellId = call.parameters["cellId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid cell ID"))

            val center = s2Service.cellToCenter(cellId)
            val boundary = s2Service.cellToBoundary(cellId)

            call.respond(
                CellDetailsResponse(
                    cellId = cellId,
                    center = CenterPoint(latitude = center.first, longitude = center.second),
                    boundary = boundary.map { Point(latitude = it.first, longitude = it.second) }
                )
            )
        }
    }
}