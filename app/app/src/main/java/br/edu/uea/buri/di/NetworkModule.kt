package br.edu.uea.buri.di

import android.content.SharedPreferences
import br.edu.uea.buri.BuildConfig
import br.edu.uea.buri.data.BasicAuthInterceptor
import br.edu.uea.buri.data.BuriApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBasicAuthInterceptor(sharedPreferences: SharedPreferences): BasicAuthInterceptor {
        return BasicAuthInterceptor(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: BasicAuthInterceptor): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
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