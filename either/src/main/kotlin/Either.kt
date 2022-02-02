package com.javiersc.ktor.serialization.repro.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement

sealed class Either<out L, out R> {

    data class Left<out L>(val left: L) : Either<L, Nothing>()

    data class Right<out R>(val right: R) : Either<Nothing, R>()
}

class EitherSerializer<L, R>(
    private val leftSerializer: KSerializer<L>,
    private val rightSerializer: KSerializer<R>,
) : KSerializer<Either<L, R>> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("NetworkEitherSerializer") {
            element("left", leftSerializer.descriptor)
            element("right", rightSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): Either<L, R> {

        require(decoder is JsonDecoder) { "only works in JSON format" }
        val element: JsonElement = decoder.decodeJsonElement()

        return try {
            Either.Right(decoder.json.decodeFromJsonElement(rightSerializer, element))
        } catch (throwable: Throwable) {
            Either.Left(decoder.json.decodeFromJsonElement(leftSerializer, element))
        }
    }

    override fun serialize(encoder: Encoder, value: Either<L, R>) {
        when (value) {
            is Either.Left -> encoder.encodeSerializableValue(leftSerializer, value.left)
            is Either.Right -> encoder.encodeSerializableValue(rightSerializer, value.right)
        }
    }
}
