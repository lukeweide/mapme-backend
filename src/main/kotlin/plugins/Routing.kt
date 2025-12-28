package com.mapme.plugins

import data.repositories.PhotoRepository
import com.mapme.data.repositories.UserRepository
import com.mapme.data.services.S2Service
import routes.gridRoutes
import com.mapme.routes.healthRoutes
import com.mapme.routes.photoRoutes
import com.mapme.routes.userRoutes
import com.mapme.routes.visitedCellsRoutes
import data.repositories.VisitedCellsRepository
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    val userRepository = UserRepository()
    val photoRepository = PhotoRepository()
    val visitedCellsRepository = VisitedCellsRepository()

    val s2Service = S2Service()

    routing {
        healthRoutes()
        userRoutes(userRepository, visitedCellsRepository)
        photoRoutes(photoRepository, s2Service)
        gridRoutes(s2Service)
        visitedCellsRoutes(visitedCellsRepository, s2Service)

        get("/") {
            call.respondText("Hello World!")
        }
        get<Articles> { article ->
            // Get all articles ...
            call.respond("List of articles sorted starting from ${article.sort}")
        }
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
