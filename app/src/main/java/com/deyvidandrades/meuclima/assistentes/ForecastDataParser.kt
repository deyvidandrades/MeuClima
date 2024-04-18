package com.deyvidandrades.meuclima.assistentes

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.objetos.ForecastCurrent
import com.deyvidandrades.meuclima.objetos.ForecastDaily
import com.deyvidandrades.meuclima.objetos.ForecastHourly
import com.google.gson.JsonParser
import java.net.URL

object ForecastDataParser {

    fun getApiUrl(lat: Double, lon: Double): URL {
        return URL("""https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,is_day,rain,showers,weather_code,wind_speed_10m&hourly=temperature_2m,precipitation_probability,precipitation,rain,weather_code,is_day&daily=weather_code,temperature_2m_max,temperature_2m_min&timeformat=unixtime&timezone=auto&forecast_hours=24""")
    }

    fun getAlertCodes() = arrayListOf(65, 66, 67, 75, 82, 86, 95, 96, 99)

    fun getCode(code: Int): String {
        return when (code) {
            0 -> "Céu limpo"
            1 -> "Algumas nuvens"
            2 -> "Parcialmente nublado"
            3 -> "Nublado"
            45 -> "Nevoeiro"
            48 -> "Nevoeiro com gelo"
            51 -> "Chuvisco leve"
            53 -> "Chuvisco moderado"
            55 -> "Chuvisco forte"
            56, 57 -> "Chuvisco congelante"
            61 -> "Chuva leve"
            63 -> "Chuva moderada"
            65 -> "Chuva forte"
            66, 67 -> "Chuva congelante"
            71 -> "Nevando fraco"
            73 -> "Nevando"
            75 -> "Nevando muito"
            77 -> "Grãos de neve"
            80 -> "Pancadas de chuva leve"
            81 -> "Pancada de chuva"
            82 -> "Pancada de chuva forte"
            85 -> "Nevasca"
            86 -> "Nevasca forte"
            95 -> "Tempestade"
            96, 99 -> "Tempestade de granizo"
            else -> "Condição meteorológica desconhecida"
        }
    }

    fun getWeatherDrawable(context: Context, code: Int, isDia: Boolean): Drawable {
        return AppCompatResources.getDrawable(
            context, when (code) {
                0 -> if (isDia) R.drawable.clima_limpo_dia else R.drawable.clima_limpo_noite
                1, 2 -> if (isDia) R.drawable.clima_nublado_dia else R.drawable.clima_nublado_noite
                3 -> R.drawable.clima_nublado
                45, 48 -> if (isDia) R.drawable.clima_neblina else R.drawable.clima_neblina_noite
                51, 53, 55, 56, 57 -> R.drawable.clima_chuva_fraca
                61 -> R.drawable.clima_chuva_fraca
                63 -> R.drawable.clima_chuva_moderada
                65, 66, 67 -> R.drawable.clima_chuva_forte
                71, 73, 75 -> R.drawable.clima_neve
                77 -> R.drawable.clima_neve_forte
                80, 81 -> if (isDia) R.drawable.clima_pancada_dia else R.drawable.clima_pancada_noite
                82 -> R.drawable.clima_tempestade
                85, 86 -> if (isDia) R.drawable.clima_pancada_neve_dia else R.drawable.clima_pancada_neve_noite
                95 -> R.drawable.clima_tempestade_raio
                96, 99 -> R.drawable.clima_neve
                else -> R.drawable.clima_sem_dados
            }
        )!!
    }

    fun getForecast(
        dataRaw: String,
        listener: (current: ForecastCurrent, hourly: ArrayList<ForecastHourly>, week: ArrayList<ForecastDaily>) -> Unit
    ) {
        try {
            val jsonObject = JsonParser.parseString(dataRaw).getAsJsonObject()
            val dataCurrent = jsonObject["current"].asJsonObject
            val dataDaily = jsonObject["daily"].asJsonObject
            val dataHourly = jsonObject["hourly"].asJsonObject

            val proximasHoras = ArrayList<ForecastHourly>()
            for (i in 0..23) {
                proximasHoras.add(
                    ForecastHourly(
                        dataHourly["time"].asJsonArray[i].asLong * 1000,
                        dataHourly["temperature_2m"].asJsonArray[i].asDouble.toInt(),
                        dataHourly["precipitation_probability"].asJsonArray[i].asInt,
                        dataHourly["weather_code"].asJsonArray[i].asInt,
                        dataHourly["is_day"].asJsonArray[i].toString().toInt() == 1
                    )
                )
            }

            val proximosDias = ArrayList<ForecastDaily>()
            for (i in 0..6) {
                proximosDias.add(
                    ForecastDaily(
                        dataDaily["time"].asJsonArray[i].asLong * 1000,
                        dataDaily["temperature_2m_max"].asJsonArray[i].asDouble.toInt(),
                        dataDaily["temperature_2m_min"].asJsonArray[i].asDouble.toInt(),
                        dataDaily["weather_code"].asJsonArray[i].asInt
                    )
                )
            }

            val currentForecast = ForecastCurrent(
                dataCurrent["time"].asLong * 1000,
                dataCurrent["temperature_2m"].asInt,
                dataCurrent["relative_humidity_2m"].asInt,
                dataCurrent["is_day"].toString().toInt() == 1,
                dataCurrent["rain"].asInt == 1,
                dataCurrent["showers"].asInt == 1,
                dataCurrent["weather_code"].asInt,
                dataCurrent["wind_speed_10m"].asDouble.toInt(),
                proximasHoras[0].getPrecipitacao().toInt()
            )

            listener.invoke(currentForecast, proximasHoras, proximosDias)
        } catch (_: Exception) {

        }
    }
}