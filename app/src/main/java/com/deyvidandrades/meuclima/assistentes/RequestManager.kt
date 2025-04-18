package com.deyvidandrades.meuclima.assistentes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object RequestManager {

    suspend fun fazerRequisicao(url: URL): String = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url.toString()).openConnection() as HttpURLConnection
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            ""
        }
    }
}