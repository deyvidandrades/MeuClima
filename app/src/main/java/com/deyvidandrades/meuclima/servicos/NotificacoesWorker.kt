package com.deyvidandrades.meuclima.servicos

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.ForecastDataParser
import com.deyvidandrades.meuclima.assistentes.NotificacoesUtil
import com.deyvidandrades.meuclima.assistentes.Persistencia
import com.deyvidandrades.meuclima.assistentes.RequestManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificacoesWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Persistencia.getInstance(applicationContext)
        val lat = Persistencia.latitude
        val lon = Persistencia.longitude

        if ((lat != 0.0 && lon != 0.0) && Persistencia.notificacao) {
            val result: String = RequestManager.fazerRequisicao(ForecastDataParser.getApiUrl(lat, lon))

            ForecastDataParser.getForecast(result) { current, hourly, _ ->
                NotificacoesUtil.enviarNotificacao(
                    applicationContext,
                    "${current.getCodeInt()}, ${current.getTemperatura()}º",
                    applicationContext.getString(R.string.veja_a_previsao_completa),
                    ForecastDataParser.getWeatherDrawable(applicationContext, current.getCodeInt(), current.isDia()),
                    NotificacoesUtil.Tipo.PREVISAO
                )

                for (item in hourly)
                    if (item.getCodeInt() in ForecastDataParser.getAlertCodes()) {
                        val evento = ForecastDataParser.getCode(applicationContext, item.getCodeInt())
                        val horario = SimpleDateFormat("HH", Locale.getDefault()).format(Date(item.getData()))
                        val icone = ForecastDataParser.getWeatherDrawable(
                            applicationContext,
                            item.getCodeInt(),
                            item.isDay()
                        )

                        NotificacoesUtil.enviarNotificacao(
                            applicationContext,
                            applicationContext.getString(R.string.alerta_de, evento),
                            applicationContext.getString(R.string.esperada_s_horas, evento, horario),
                            icone,
                            NotificacoesUtil.Tipo.ALERTA
                        )
                        break
                    }
            }
        }
        return Result.success()
    }
}