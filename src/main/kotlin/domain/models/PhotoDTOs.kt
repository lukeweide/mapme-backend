package com.mapme.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CreatePhotoRequest(
    val latitude: Double,
    val longitude: Double,
    val cameraMake: String? = null,
    val cameraModel: String? = null,
    val takenAt: Long? = null  // Unix timestamp
)

@Serializable
data class PhotoResponse(
    val id: String,
    val userId: String,
    val filePath: String,
    val thumbnailPath: String?,
    val latitude: Double,
    val longitude: Double,
    val s2CellId: Long,
    val cameraMake: String?,
    val cameraModel: String?,
    val takenAt: Long?,
    val uploadedAt: Long
)

@Serializable
data class PhotoMarkerResponse(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val thumbnailUrl: String?
)