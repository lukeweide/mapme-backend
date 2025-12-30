package data.repositories

import com.mapme.data.database.DatabaseFactory.dbQuery
import com.mapme.data.database.tables.Photos
import com.mapme.data.database.tables.VisitedCells
import com.mapme.domain.models.Photo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class PhotoRepository {

    /**
     * Create a new photo entry
     */
    suspend fun create(
        userId: UUID,
        filePath: String,
        thumbnailPath: String?,
        latitude: Double,
        longitude: Double,
        s2CellId: Long,
        cameraMake: String?,
        cameraModel: String?,
        takenAt: Instant?
    ): Photo = dbQuery {
        val id = UUID.randomUUID()

        Photos.insert {
            it[Photos.id] = id
            it[Photos.userId] = userId
            it[Photos.filePath] = filePath
            it[Photos.thumbnailPath] = thumbnailPath
            it[Photos.latitude] = latitude
            it[Photos.longitude] = longitude
            it[Photos.s2CellId] = s2CellId
            it[Photos.cameraMake] = cameraMake
            it[Photos.cameraModel] = cameraModel
            it[Photos.takenAt] = takenAt
        }

        // Update or create visited cell
        updateVisitedCell(userId, s2CellId)

        Photo(
            id = id,
            userId = userId,
            filePath = filePath,
            thumbnailPath = thumbnailPath,
            latitude = latitude,
            longitude = longitude,
            s2CellId = s2CellId,
            cameraMake = cameraMake,
            cameraModel = cameraModel,
            takenAt = takenAt,
            uploadedAt = Clock.System.now()
        )
    }

    /**
     * Find photo by ID
     */
    suspend fun findById(photoId: UUID): Photo? = dbQuery {
        Photos.selectAll()
            .where { Photos.id eq photoId }
            .map { rowToPhoto(it) }
            .singleOrNull()
    }

    /**
     * Get photos in viewport (bounding box)
     */
    suspend fun findInViewport(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): List<Photo> = dbQuery {
        Photos.selectAll()
            .where {
                (Photos.latitude greaterEq minLat) and
                        (Photos.latitude lessEq maxLat) and
                        (Photos.longitude greaterEq minLon) and
                        (Photos.longitude lessEq maxLon)
            }
            .map { rowToPhoto(it) }
    }

    /**
     * Delete photo
     */
    suspend fun delete(photoId: UUID): Boolean = dbQuery {
        Photos.deleteWhere { id eq photoId } > 0
    }

    /**
     * Updates or creates a visited cell entry
     */
    private suspend fun updateVisitedCell(userId: UUID, s2CellId: Long) = dbQuery {
        val now = Clock.System.now()
        val existing = VisitedCells.selectAll()
            .where { (VisitedCells.userId eq userId) and (VisitedCells.s2CellId eq s2CellId) }
            .singleOrNull()

        if (existing != null) {
            // Update existing
            VisitedCells.update({
                (VisitedCells.userId eq userId) and (VisitedCells.s2CellId eq s2CellId)
            }) {
                it[photoCount] = existing[photoCount] + 1
                it[lastVisitedAt] = now
            }
        } else {
            // Create new
            VisitedCells.insert {
                it[id] = UUID.randomUUID()
                it[VisitedCells.userId] = userId
                it[VisitedCells.s2CellId] = s2CellId
                it[photoCount] = 1
                it[firstVisitedAt] = now
                it[lastVisitedAt] = now
            }
        }
    }

    private fun rowToPhoto(row: ResultRow): Photo {
        return Photo(
            id = row[Photos.id],
            userId = row[Photos.userId],
            filePath = row[Photos.filePath],
            thumbnailPath = row[Photos.thumbnailPath],
            latitude = row[Photos.latitude],
            longitude = row[Photos.longitude],
            s2CellId = row[Photos.s2CellId],
            cameraMake = row[Photos.cameraMake],
            cameraModel = row[Photos.cameraModel],
            takenAt = row[Photos.takenAt],
            uploadedAt = row[Photos.uploadedAt]
        )
    }
}