package com.alexeymerov.radiostations.core.remote.di


import android.content.Context
import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.core.common.ProjectConst
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.interceptor.JsonResponseInterceptor
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = NetworkDefaults.getJson()

    @Provides
    @Singleton
    fun provideMediaType(): MediaType = NetworkDefaults.getMediaType()

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json, mediaType: MediaType): Converter.Factory = NetworkDefaults.getConverterFactory(json, mediaType)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideForceJsonInterceptor(): JsonResponseInterceptor = NetworkDefaults.getJsonInterceptor()

    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .createShortcut(true)
            .build()

        return chuckerInterceptor
    }

    @Provides
    @Singleton
    @Backend(Server.Radio)
    fun provideRadioOkHttpClient(
        jsonResponseInterceptor: JsonResponseInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return NetworkDefaults.getOkHttpClient(
            interceptors = arrayOf(jsonResponseInterceptor, loggingInterceptor, chuckerInterceptor)
        )
    }

    @Provides
    @Singleton
    @Backend(Server.Countries)
    fun provideCountriesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return NetworkDefaults.getOkHttpClient(
            interceptors = arrayOf(loggingInterceptor, chuckerInterceptor)
        )
    }

    @Provides
    @Singleton
    @Backend(Server.Radio)
    fun provideRadioRetrofit(
        @Backend(Server.Radio) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(ProjectConst.BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(
        @Backend(Server.Countries) okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .okHttpClient(okHttpClient)
            .serverUrl(NetworkDefaults.COUNTRIES_URL)
            .build()
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Backend(val server: Server)

internal enum class Server {
    Radio,
    Countries,
}
