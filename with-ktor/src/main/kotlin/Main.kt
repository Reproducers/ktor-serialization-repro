package com.javiersc.ktor.serialization.repro.with.ktor

import com.javiersc.ktor.serialization.repro.either.Either
import com.javiersc.ktor.serialization.repro.either.EitherSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

suspend fun main() {
    val dogJson =
        """
            {
                "age": 8,
                "name": "Auri"
            }
        """.trimIndent()
    println("dog:")
    printlnN(
        client(dogJson, HttpStatusCode.OK)
            .get("https://example.com")
            .body<Either<ErrorDTO, DogDTO>>()
    )

    val errorJson =
        """
            {
                "message": "Some error"
            }
        """.trimIndent()
    println("error:")
    printlnN(
        client(errorJson, HttpStatusCode.NotFound)
            .get("https://example.com")
            .body<Either<ErrorDTO, DogDTO>>()
    )

    val emptyErrorJson = "{ }"
    println("empty error:")
    printlnN(
        client(emptyErrorJson, HttpStatusCode.NotFound)
            .get("https://example.com")
            .body<Either<ErrorDTO, DogDTO>>()
    )
}

fun client(content: String, status: HttpStatusCode): HttpClient {
    val mockEngine = MockEngine {
        respond(
            content = content,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }

    return HttpClient(mockEngine) {
        install(ContentNegotiation) { json(json) }
        expectSuccess = false
    }
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
