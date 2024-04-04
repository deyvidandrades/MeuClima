package com.deyvidandrades.meuclima.servicos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meuclima.assistentes.WorkManagerUtil

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            WorkManagerUtil.iniciarWorker(context, WorkManagerUtil.Tipo.NOTIFICACAO)
        }
    }
}