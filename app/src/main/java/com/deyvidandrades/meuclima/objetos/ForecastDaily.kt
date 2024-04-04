package com.deyvidandrades.meuclima.objetos

data class ForecastDaily(
    private val data: Long,
    private val temperaturaMax: Int,
    private val temperaturaMin: Int,
    private val code: Int,
) {

    fun getData() = data

    fun getTemperaturaMax() = temperaturaMax

    fun getTemperaturaMin() = temperaturaMin

    fun getCodeInt() = code

}
