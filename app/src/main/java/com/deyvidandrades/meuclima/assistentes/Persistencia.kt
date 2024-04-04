package com.deyvidandrades.meuclima.assistentes

import android.content.Context
import android.content.SharedPreferences

object Persistencia {

    var isDarkTheme: Boolean = false
    var notificacao: Boolean = true
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private var preferences: SharedPreferences? = null

    enum class Paths { IS_DARK_THEME, NOTIFICACOES, LATITUDE, LONGITUDE }

    fun getInstance(context: Context) {
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)
        carregarDados()
    }

    /*FLUXO DADOS*/

    private fun carregarDados() {
        if (preferences != null) {
            isDarkTheme = preferences!!.getBoolean(Paths.IS_DARK_THEME.name.lowercase(), false)
            notificacao = preferences!!.getBoolean(Paths.NOTIFICACOES.name.lowercase(), true)

            latitude = preferences!!.getString(Paths.LATITUDE.name.lowercase(), "0.0")?.toDouble() ?: 0.0
            longitude = preferences!!.getString(Paths.LONGITUDE.name.lowercase(), "0.0")?.toDouble() ?: 0.0
        }
    }

    private fun salvarDados() {
        if (preferences != null) {
            with(preferences!!.edit()) {
                putBoolean(Paths.IS_DARK_THEME.name.lowercase(), isDarkTheme)
                putBoolean(Paths.NOTIFICACOES.name.lowercase(), notificacao)

                putString(Paths.LATITUDE.name.lowercase(), latitude.toString())
                putString(Paths.LONGITUDE.name.lowercase(), longitude.toString())

                //putString(Paths.FEED_GROUPS.name.lowercase(), Gson().toJson(arrayFeedGroups))
                commit()
            }

            carregarDados()
        }
    }

    /*FLUXO SETTINGS*/
    fun setLocalizacao(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
        salvarDados()
    }

    fun setDarkTheme() {
        isDarkTheme = !isDarkTheme
        salvarDados()
    }

    fun setNotificacoes() {
        notificacao = !notificacao
        salvarDados()
    }
}