package com.mapme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Photo(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val filePath: String,
    val thumbnailPath: String?,
    val latitude: Double,
    val longitude: Double,
    val s2CellId: Long,
    val cameraMake: String?,
    val cameraModel: String?,
    @Serializable(with = InstantSerializer::class)
    val takenAt: Instant?,
    @Serializable(with = InstantSerializer::class)
    val uploadedAt: Instant
)