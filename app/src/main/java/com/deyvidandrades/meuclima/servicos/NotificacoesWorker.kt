package com.deyvidandrades.meuclima.servicos

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.deyvidandrades.meuclima.assistentes.NotificacoesUtil
import com.deyvidandrades.meuclima.assistentes.Persistencia

class NotificacoesWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Persistencia.getInstance(applicationContext)

        if ((Persistencia.latitude != 0.0 && Persistencia.longitude != 0.0) && Persistencia.notificacao)
            NotificacoesUtil.enviarNotificacao(applicationContext)

        return Result.success()
    }
}