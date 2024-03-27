package com.alexeymerov.radiostations.core.remote.di


import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseHttpClient(): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(NetworkDefaults.getJson())
            register(
                contentType = ContentType.Audio.Any,
                converter = KotlinxSerializationConverter(NetworkDefaults.getJson())
            )
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
        install(Logging) {
            logger = Logger.TIMBER
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }
    }

    @Provides
    @Singleton
    @Backend(Server.Radio)
    fun provideRadioHttpClient(baseHttpClient: HttpClient): HttpClient = baseHttpClient.config {
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
                host = NetworkDefaults.RADIO_URL_HOST
                parameters.append(
                    name = NetworkDefaults.QUERY_RENDER_NAME,
                    value = NetworkDefaults.QUERY_RENDER_JSON_PARAMETER
                )
            }
        }
    }

    @Provides
    @Singleton
    @Backend(Server.Countries)
    fun provideCountriesHttpClient(baseHttpClient: HttpClient): HttpClient = baseHttpClient.config {
        defaultRequest {
            contentType(ContentType.Application.Json)
            url(NetworkDefaults.COUNTRIES_URL)
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Backend(val server: Server)

internal enum class Server {
    Radio,
    Countries,
}

internal val Logger.Companion.TIMBER
    get() = object : Logger {
        override fun log(message: String) = Timber.d(
            "\n-------------------------" +
                "\n--- NETWORK LOG START ---" +
                "\n-------------------------" +
                "\n$message" +
                "\n-----------------------" +
                "\n--- NETWORK LOG END ---" +
                "\n-----------------------\n"
        )
    }