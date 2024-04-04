package com.deyvidandrades.meuclima.assistentes

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.activities.MainActivity
import kotlinx.coroutines.runBlocking

object NotificacoesUtil {
    private const val CHANNEL_ID = "meu_clima_1"
    private const val NOTIFICATION_ID = 5

    fun criarCanalDeNotificacoes(context: Context) {

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), importance).apply {
            description = context.getString(R.string.notificacao_do_clima)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun enviarNotificacao(context: Context) {
        Persistencia.getInstance(context)

        var result: String
        runBlocking {
            result = RequestManager.fazerRequisicao(
                ForecastDataParser.getApiUrl(
                    Persistencia.latitude,
                    Persistencia.longitude
                )
            )
        }

        ForecastDataParser.getForecast(result) { current, _, _ ->
            //CRIANDO A NOTIFICACAO todo
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setColorized(true)
                .setShowWhen(true)
                .setColor(context.getColor(if (Persistencia.isDarkTheme) R.color.accentColorDarkTheme else R.color.accentColorLightTheme))
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("${current.getCode()}, ${current.getTemperatura()}º")
                .setContentText(context.getString(R.string.veja_a_previsao_completa))
                .setSmallIcon(R.drawable.round_cloud_queue_24)
                .setLargeIcon(
                    ForecastDataParser.getWeatherDrawable(context, current.getCodeInt(), current.isDia()).toBitmap()
                )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java).apply {
                            //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        },
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@with
                }
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}