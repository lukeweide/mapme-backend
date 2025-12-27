package com.mapme.data.database

import com.mapme.data.database.tables.Cities
import com.mapme.data.database.tables.Photos
import com.mapme.data.database.tables.PointOfInterests
import com.mapme.data.database.tables.UserPointOfInterests
import com.mapme.data.database.tables.Users
import com.mapme.data.database.tables.VisitedCells
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val database = Database.connect(createHikariDataSource())

        // Tabellen erstellen (falls nicht existieren)
        transaction(database) {
            SchemaUtils.create(
                Users,
                Photos,
                VisitedCells,
                Cities,
                PointOfInterests,
                UserPointOfInterests
            )
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/mapme"
            username = System.getenv("DB_USER") ?: "mapme"
            password = System.getenv("DB_PASSWORD") ?: "dev_password"
            maximumPoolSize = 10 // limits parallel DB queries to 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}