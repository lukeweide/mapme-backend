package com.mapme.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String
)

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: Long
)

@Serializable
data class UserStatsResponse(
    val totalCells: Int,
    val totalPhotos: Int,
    val countries: Int,
    val cities: Int
)