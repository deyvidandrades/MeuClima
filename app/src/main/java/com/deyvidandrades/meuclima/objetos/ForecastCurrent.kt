@file:Suppress("unused")

package com.deyvidandrades.meuclima.objetos

data class ForecastCurrent(
    private val data: Long,
    private val temperatura: Int,
    private val humidade: Int,
    private val isDia: Boolean,
    private val isChuva: Boolean,
    private val isPancadas: Boolean,
    private val code: Int,
    private val vento: Int,
    private val precipitacao: Int
) {
    enum class Eventos { NONE, CHUVA, PANCADAS_DE_CHUVA }

    fun getTemperatura() = temperatura

    fun getHumidade() = humidade

    fun getEventos(): Eventos {
        return if (isPancadas) Eventos.PANCADAS_DE_CHUVA else if (isChuva) Eventos.CHUVA else Eventos.NONE
    }

    fun getVento() = vento

    fun getPrecipitacao() = precipitacao

    fun isDia() = isDia

    fun getCodeInt() = code
}
