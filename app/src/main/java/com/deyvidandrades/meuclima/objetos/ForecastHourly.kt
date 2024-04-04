package com.deyvidandrades.meuclima.objetos

data class ForecastHourly(
    private val data: Long,
    private val temperatura: Int,
    private val precipitacao: Int,
    private val code: Int,
    private val isDay: Boolean
) {

    fun getData() = data

    fun getTemperatura() = temperatura

    fun getPrecipitacao() = precipitacao.toDouble()

    fun getCodeInt() = code

    fun isDay() = isDay
}
