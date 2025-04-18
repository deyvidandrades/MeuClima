package com.deyvidandrades.meuclima.dialogos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.net.toUri
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.AnimacaoBotao
import com.deyvidandrades.meuclima.assistentes.Persistencia
import com.deyvidandrades.meuclima.assistentes.WorkManagerUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch

class DialogoConfiguracoes : BottomSheetDialogFragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_configuracoes, container, false)

        val btnConcluido: Button = dialogoView.findViewById(R.id.btn_concluido)
        val switchNotificacoes: MaterialSwitch = dialogoView.findViewById(R.id.switch_notificacoes)
        val tvTermos: TextView = dialogoView.findViewById(R.id.tv_termos)
        val tvVersao: TextView = dialogoView.findViewById(R.id.tv_versao)
        val tvApi: TextView = dialogoView.findViewById(R.id.tv_api)

        switchNotificacoes.isChecked = Persistencia.notificacao

        btnConcluido.setOnClickListener {
            if (switchNotificacoes.isChecked) {
                WorkManagerUtil.stopWorker(requireContext(), WorkManagerUtil.Tipo.NOTIFICACAO)
                WorkManagerUtil.iniciarWorker(requireContext(), WorkManagerUtil.Tipo.NOTIFICACAO)
            }

            if (switchNotificacoes.isChecked != Persistencia.notificacao)
                Persistencia.setNotificacoes()

            dismiss()
        }

        tvTermos.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, "https://deyvidandrades.github.io/MeuClima/termos/".toUri()))
        }

        tvApi.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, "https://open-meteo.com/".toUri()))
        }

        val info =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, PackageManager.GET_ACTIVITIES)

        tvVersao.text = "${requireContext().getString(R.string.app_name)} v${info.versionName}"

        return dialogoView
    }
}