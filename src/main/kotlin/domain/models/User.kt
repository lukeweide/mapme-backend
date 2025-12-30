package com.mapme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val email: String,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant
)

// UUID Serializer
object UUIDSerializer : kotlinx.serialization.KSerializer<UUID> {
    override val descriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("UUID", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: UUID) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): UUID = UUID.fromString(decoder.decodeString())
}

// Instant Serializer
object InstantSerializer : kotlinx.serialization.KSerializer<Instant> {
    override val descriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("Instant", kotlinx.serialization.descriptors.PrimitiveKind.LONG)
    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilliseconds())
    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): Instant = Instant.fromEpochMilliseconds(decoder.decodeLong())
}