package com.mapme.data.repositories

import com.mapme.data.database.DatabaseFactory.dbQuery
import com.mapme.data.database.tables.Users
import com.mapme.domain.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class UserRepository {

    suspend fun create(username: String, email: String): User = dbQuery {
        val id = UUID.randomUUID()

        Users.insert {
            it[Users.id] = id
            it[Users.username] = username
            it[Users.email] = email
        }

        User(
            id = id,
            username = username,
            email = email,
            createdAt = kotlinx.datetime.Clock.System.now()
        )
    }

    suspend fun findById(userId: UUID): User? = dbQuery {
        Users.selectAll()
            .where { Users.id eq userId }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun findByEmail(email: String): User? = dbQuery {
        Users.selectAll()
            .where { Users.email eq email }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            createdAt = row[Users.createdAt]
        )
    }
}