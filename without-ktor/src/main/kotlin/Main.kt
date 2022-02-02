package com.javiersc.ktor.serialization.repro.without.ktor

import com.javiersc.ktor.serialization.repro.either.Either
import com.javiersc.ktor.serialization.repro.either.EitherSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

fun main() {
    val dogJson =
        """
            {
                "age": 8,
                "name": "Auri"
            }
        """.trimIndent()
    println("dog - decodeFromString<Either<ErrorDTO, DogDTO>>:")
    printlnN(json.decodeFromString<Either<ErrorDTO, DogDTO>>(dogJson))

    val dogDTO = DogDTO(age = 4, name = "Roni")
    println("dog - encodeToString<Either<ErrorDTO, DogDTO>>:")
    printlnN(json.encodeToString<Either<ErrorDTO, DogDTO>>(Either.Right(dogDTO)))

    val errorJson =
        """
            {
                "message": "Some error"
            }
        """.trimIndent()
    println("error - decodeFromString<Either<ErrorDTO, DogDTO>>:")
    printlnN(json.decodeFromString<Either<ErrorDTO, DogDTO>>(errorJson))

    val emptyErrorJson = "{ }"
    println("empty error - decodeFromString<Either<ErrorDTO, DogDTO>>:")
    printlnN(json.decodeFromString<Either<ErrorDTO, DogDTO>>(emptyErrorJson))
}

@Serializable data class DogDTO(val age: Int, val name: String)

@Serializable data class ErrorDTO(val message: String = "Some default error")

private val json = Json {
    prettyPrint = true
    encodeDefaults = true
    serializersModule =
        SerializersModule {
            contextual(Either::class) { serializers: List<KSerializer<*>> ->
                EitherSerializer(serializers[0], serializers[1])
            }
        }
}

fun printlnN(message: Any) = println("$message\n")
