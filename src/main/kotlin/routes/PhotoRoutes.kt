package com.mapme.routes

import com.mapme.data.repositories.PhotoRepository
import com.mapme.data.services.S2Service
import com.mapme.domain.models.CreatePhotoRequest
import com.mapme.domain.models.PhotoMarkerResponse
import com.mapme.domain.models.PhotoResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*                      // ← NEU!
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.datetime.Instant
import java.io.File
import java.util.*

fun Route.photoRoutes(
    photoRepository: PhotoRepository,
    s2Service: S2Service
) {

    route("/api/v1/photos") {

        // POST /api/v1/photos - Upload Photo
        post {
            val userId = UUID.fromString("d8f9959c-0ab8-44aa-ad10-f03ae2764fde")

            val multipart = call.receiveMultipart()
            var photoFile: File? = null
            var request: CreatePhotoRequest? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = "${UUID.randomUUID()}.jpg"
                        val uploadDir = File("data/uploads")
                        uploadDir.mkdirs()

                        val file = File(uploadDir, fileName)
                        photoFile = file

                        part.provider().toInputStream().use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    is PartData.FormItem -> {
                        if (part.name == "metadata") {
                            request = kotlinx.serialization.json.Json.decodeFromString<CreatePhotoRequest>(part.value)
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (photoFile == null || request == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing file or metadata"))
                return@post
            }

            val s2CellId = s2Service.coordinatesToCell(request!!.latitude, request!!.longitude)

            val photo = photoRepository.create(
                userId = userId,
                filePath = photoFile!!.absolutePath,
                thumbnailPath = null,
                latitude = request!!.latitude,
                longitude = request!!.longitude,
                s2CellId = s2CellId,
                cameraMake = request!!.cameraMake,
                cameraModel = request!!.cameraModel,
                takenAt = request!!.takenAt?.let { Instant.fromEpochMilliseconds(it) }
            )

            call.respond(
                HttpStatusCode.Created,
                PhotoResponse(
                    id = photo.id.toString(),
                    userId = photo.userId.toString(),
                    filePath = photo.filePath,
                    thumbnailPath = photo.thumbnailPath,
                    latitude = photo.latitude,
                    longitude = photo.longitude,
                    s2CellId = photo.s2CellId,
                    cameraMake = photo.cameraMake,
                    cameraModel = photo.cameraModel,
                    takenAt = photo.takenAt?.toEpochMilliseconds(),
                    uploadedAt = photo.uploadedAt.toEpochMilliseconds()
                )
            )
        }

        get("/{id}") {
            val photoId = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

            val photo = photoRepository.findById(photoId)
            if (photo == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Photo not found"))
                return@get
            }

            call.respond(
                PhotoResponse(
                    id = photo.id.toString(),
                    userId = photo.userId.toString(),
                    filePath = photo.filePath,
                    thumbnailPath = photo.thumbnailPath,
                    latitude = photo.latitude,
                    longitude = photo.longitude,
                    s2CellId = photo.s2CellId,
                    cameraMake = photo.cameraMake,
                    cameraModel = photo.cameraModel,
                    takenAt = photo.takenAt?.toEpochMilliseconds(),
                    uploadedAt = photo.uploadedAt.toEpochMilliseconds()
                )
            )
        }

        get("/markers") {
            val minLat = call.request.queryParameters["minLat"]?.toDoubleOrNull()
            val maxLat = call.request.queryParameters["maxLat"]?.toDoubleOrNull()
            val minLon = call.request.queryParameters["minLon"]?.toDoubleOrNull()
            val maxLon = call.request.queryParameters["maxLon"]?.toDoubleOrNull()

            if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing bounds parameters"))
                return@get
            }

            val photos = photoRepository.findInViewport(minLat, maxLat, minLon, maxLon)

            call.respond(
                photos.map { photo ->
                    PhotoMarkerResponse(
                        id = photo.id.toString(),
                        latitude = photo.latitude,
                        longitude = photo.longitude,
                        thumbnailUrl = photo.thumbnailPath?.let { "/api/v1/photos/${photo.id}/thumbnail" }
                    )
                }
            )
        }

        delete("/{id}") {
            val photoId = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

            val deleted = photoRepository.delete(photoId)
            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Photo not found"))
                return@delete
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}