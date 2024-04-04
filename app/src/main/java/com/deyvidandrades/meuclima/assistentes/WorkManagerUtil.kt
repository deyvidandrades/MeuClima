package com.deyvidandrades.meuclima.assistentes

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.deyvidandrades.meuclima.servicos.NotificacoesWorker
import java.util.concurrent.TimeUnit

object WorkManagerUtil {
    enum class Tipo { NOTIFICACAO }

    fun iniciarWorker(context: Context, workerId: Tipo) {
        val workRequest = PeriodicWorkRequestBuilder<NotificacoesWorker>(8, TimeUnit.HOURS).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workerId.name.lowercase(),
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopWorker(context: Context, workerId: Tipo) {
        WorkManager.getInstance(context).cancelUniqueWork(workerId.name.lowercase())
    }
}