package br.edu.uea.buri.data

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.util.Base64
import android.util.Log
import javax.inject.Inject

class BasicAuthInterceptor @Inject  constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    private var username = sharedPreferences.getString("username", "") ?: ""
    private var password = sharedPreferences.getString("password", "") ?: ""
    private var credentials: String = "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)

    override fun intercept(chain: Interceptor.Chain): Response {

        //Preparação dos dados
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        credentials = "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)

        val original : Request = chain.request()
        val requestBuilder : Request.Builder = original.newBuilder().header("Authorization", credentials)
        val request : Request = requestBuilder.build()
        return  chain.proceed(request)
    }
}