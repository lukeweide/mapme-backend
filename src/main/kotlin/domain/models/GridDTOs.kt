package domain.models

import kotlinx.serialization.Serializable

@Serializable
data class GridTilesResponse(
    val type: String = "FeatureCollection",
    val features: List<GridFeature>
)

@Serializable
data class GridFeature(
    val type: String = "Feature",
    val geometry: GridGeometry,
    val properties: GridProperties
)

@Serializable
data class GridGeometry(
    val type: String = "Polygon",
    val coordinates: List<List<List<Double>>>  // [[[lon, lat], [lon, lat], ...]]
)

@Serializable
data class GridProperties(
    val cellId: Long
)

@Serializable
data class CellDetailsResponse(
    val cellId: Long,
    val center: CenterPoint,
    val boundary: List<Point>
)

@Serializable
data class CenterPoint(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class Point(
    val latitude: Double,
    val longitude: Double
)