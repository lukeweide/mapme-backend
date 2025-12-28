package data.repositories

import com.mapme.data.database.DatabaseFactory.dbQuery
import com.mapme.data.database.tables.VisitedCells
import com.mapme.domain.models.VisitedCell
import org.jetbrains.exposed.sql.*
import java.util.*

class VisitedCellsRepository {

    /**
     * Get all visited cells for a user
     */
    suspend fun findByUserId(userId: UUID): List<VisitedCell> = dbQuery {
        VisitedCells.selectAll()
            .where { VisitedCells.userId eq userId }
            .map { rowToVisitedCell(it) }
    }

    /**
     * Get visited cells within viewport
     */
    suspend fun findByUserIdInViewport(
        userId: UUID,
        cellIds: List<Long>
    ): List<VisitedCell> = dbQuery {
        if (cellIds.isEmpty()) return@dbQuery emptyList()

        VisitedCells.selectAll()
            .where {
                (VisitedCells.userId eq userId) and
                        (VisitedCells.s2CellId inList cellIds)
            }
            .map { rowToVisitedCell(it) }
    }

    /**
     * Get user statistics
     */
    suspend fun getUserStats(userId: UUID): Map<String, Int> = dbQuery {
        val totalCells = VisitedCells.selectAll()
            .where { VisitedCells.userId eq userId }
            .count()
            .toInt()

        val totalPhotos = VisitedCells.selectAll()
            .where { VisitedCells.userId eq userId }
            .sumOf { it[VisitedCells.photoCount] }

        mapOf(
            "totalCells" to totalCells,
            "totalPhotos" to totalPhotos
        )
    }

    private fun rowToVisitedCell(row: ResultRow): VisitedCell {
        return VisitedCell(
            id = row[VisitedCells.id],
            userId = row[VisitedCells.userId],
            s2CellId = row[VisitedCells.s2CellId],
            photoCount = row[VisitedCells.photoCount],
            firstVisitedAt = row[VisitedCells.firstVisitedAt],
            lastVisitedAt = row[VisitedCells.lastVisitedAt]
        )
    }
}