package com.mapme.domain.models

import domain.models.GridGeometry
import kotlinx.serialization.Serializable

@Serializable
data class VisitedCellsResponse(
    val type: String,
    val features: List<VisitedCellFeature>
)

@Serializable
data class VisitedCellFeature(
    val type: String,
    val geometry: GridGeometry,
    val properties: VisitedCellProperties
)

@Serializable
data class VisitedCellProperties(
    val cellId: Long,
    val photoCount: Int,
    val firstVisitedAt: Long,
    val lastVisitedAt: Long
)