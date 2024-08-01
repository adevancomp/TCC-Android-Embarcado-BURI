package br.edu.uea.buri.di

import android.content.Context
import android.content.SharedPreferences
import br.edu.uea.buri.BuildConfig
import br.edu.uea.buri.data.BasicAuthInterceptor
import br.edu.uea.buri.data.BuriApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBasicAuthInterceptor(sharedPreferences: SharedPreferences): BasicAuthInterceptor {
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        return BasicAuthInterceptor(username, password)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(
                JacksonConverterFactory.create()
            )
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideBuriApiService(retrofit: Retrofit) : BuriApi{
        return retrofit.create(BuriApi::class.java)
    }
}