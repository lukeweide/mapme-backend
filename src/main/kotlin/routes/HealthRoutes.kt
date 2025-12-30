package com.mapme.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class HealthResponse(
    val status: String,
    val database: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun Route.healthRoutes() {
    get("/health") {
        try {
            val dbStatus = transaction {
                exec("SELECT 1") { }
                "connected"
            }

            val response = HealthResponse(
                status = "ok",
                database = dbStatus
            )

            call.respondText(
                Json.encodeToString(response),
                ContentType.Application.Json
            )
        } catch (e: Exception) {
            val response = HealthResponse(
                status = "error",
                database = "disconnected"
            )

            call.respondText(
                Json.encodeToString(response),
                ContentType.Application.Json,
                HttpStatusCode.ServiceUnavailable
            )
        }
    }
}