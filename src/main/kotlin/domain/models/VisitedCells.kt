package com.mapme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class VisitedCell(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val s2CellId: Long,
    val photoCount: Int,
    @Serializable(with = InstantSerializer::class)
    val firstVisitedAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val lastVisitedAt: Instant
)