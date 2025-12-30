package com.mapme.routes

import com.mapme.domain.models.CreateUserRequest
import com.mapme.domain.models.UserResponse
import com.mapme.domain.models.UserStatsResponse
import com.mapme.data.repositories.UserRepository
import data.repositories.VisitedCellsRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.userRoutes(
    userRepository: UserRepository,
    visitedCellsRepository: VisitedCellsRepository
) {

    route("/api/v1/users") {

        // POST /api/v1/users - Create User
        post {
            val request = call.receive<CreateUserRequest>()

            // Check if email already exists
            val existing = userRepository.findByEmail(request.email)
            if (existing != null) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already exists"))
                return@post
            }

            val user = userRepository.create(request.username, request.email)

            call.respond(
                HttpStatusCode.Created,
                UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    createdAt = user.createdAt.toEpochMilliseconds()
                )
            )
        }

        // GET /api/v1/users/me - Get Own Profile
        get("/me") {
            // TODO: Get userId from JWT/Session (hardcoded für jetzt)
            val userId = UUID.fromString("00000000-0000-0000-0000-000000000001")

            val user = userRepository.findById(userId)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@get
            }

            call.respond(
                UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    createdAt = user.createdAt.toEpochMilliseconds()
                )
            )
        }

        // GET /api/v1/users/me/stats - Get User Statistics
        get("/me/stats") {
            // TODO: Get userId from JWT/Session (hardcoded für jetzt)
            val userId = UUID.fromString("d8f9959c-0ab8-44aa-ad10-f03ae2764fde")

            val stats = visitedCellsRepository.getUserStats(userId)

            call.respond(
                UserStatsResponse(
                    totalCells = stats["totalCells"] ?: 0,
                    totalPhotos = stats["totalPhotos"] ?: 0,
                    countries = 0,  // TODO: Implement later
                    cities = 0      // TODO: Implement later
                )
            )
        }
    }
}