package com.aethertv.di

import com.aethertv.data.remote.AceStreamEngineClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CONNECT_TIMEOUT_MS = 10_000L
    private const val REQUEST_TIMEOUT_MS = 30_000L
    private const val SOCKET_TIMEOUT_MS = 30_000L

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient {
        return HttpClient(OkHttp) {
            // Configure OkHttp engine with timeouts
            engine {
                config {
                    connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    readTimeout(SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    writeTimeout(SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                }
            }
            
            // Configure Ktor-level timeouts
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MS
                requestTimeoutMillis = REQUEST_TIMEOUT_MS
                socketTimeoutMillis = SOCKET_TIMEOUT_MS
            }
            
            install(ContentNegotiation) {
                json(json)
            }
            
            defaultRequest {
                url("http://127.0.0.1:6878/")
            }
        }
    }

    @Provides
    @Singleton
    fun provideAceStreamEngineClient(httpClient: HttpClient): AceStreamEngineClient {
        return AceStreamEngineClient(httpClient)
    }
}
