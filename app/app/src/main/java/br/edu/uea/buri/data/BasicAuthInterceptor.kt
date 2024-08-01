package br.edu.uea.buri.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.util.Base64

class BasicAuthInterceptor(user: String, password: String) : Interceptor {
    private val credentials: String = "Basic " + Base64.encodeToString("$user:$password".toByteArray(), Base64.NO_WRAP)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original : Request = chain.request()
        val requestBuilder : Request.Builder = original.newBuilder().header("Authorization", credentials)
        val request : Request = requestBuilder.build()
        return  chain.proceed(request)
    }
}