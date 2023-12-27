package com.alexeymerov.radiostations.core.remote.di


import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.interceptor.JsonResponseInterceptor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideJsonAdapterFactory(): JsonAdapter.Factory = NetworkDefaults.getJsonAdapterFactory()

    @Provides
    @Singleton
    fun provideMoshi(factory: JsonAdapter.Factory): Moshi = NetworkDefaults.getMoshi(factory)

    @Provides
    @Singleton
    fun provideConverterFactory(moshi: Moshi): Converter.Factory = NetworkDefaults.getConverterFactory(moshi)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideForceJsonInterceptor(): JsonResponseInterceptor = NetworkDefaults.getForceJsonInterceptor()

    @Provides
    @Singleton
    @Backend(Server.Radio)
    fun provideRadioOkHttpClient(jsonResponseInterceptor: JsonResponseInterceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return NetworkDefaults.getOkHttpClient(interceptors = arrayOf(jsonResponseInterceptor, loggingInterceptor))
    }

    @Provides
    @Singleton
    @Backend(Server.Countries)
    fun provideCountriesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return NetworkDefaults.getOkHttpClient(interceptors = arrayOf(loggingInterceptor))
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
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    @Backend(Server.Countries)
    fun provideCountriesRetrofit(
        @Backend(Server.Countries) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(NetworkDefaults.COUNTRIES_URL)
            .addConverterFactory(converterFactory)
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
