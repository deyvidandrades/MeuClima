package com.deyvidandrades.meuclima.dialogos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.adaptadores.AdaptadorForecastDaily
import com.deyvidandrades.meuclima.adaptadores.AdaptadorForecastHourly
import com.deyvidandrades.meuclima.objetos.ForecastDaily
import com.deyvidandrades.meuclima.objetos.ForecastHourly
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogoForecast : BottomSheetDialogFragment() {
    var arrayDailyForecast = ArrayList<ForecastDaily>()
    var arrayHourlyForecast = ArrayList<ForecastHourly>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_forecast, container, false)

        val btnFechar: Button = dialogoView.findViewById(R.id.btn_fechar)

        //Recycler Daily
        val recyclerDaily: RecyclerView = dialogoView.findViewById(R.id.recycler_daily)
        recyclerDaily.adapter = AdaptadorForecastDaily(requireContext(), arrayDailyForecast)
        recyclerDaily.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        //Recycler Hourly
        val recyclerHourly: RecyclerView = dialogoView.findViewById(R.id.recycler_hourly)
        recyclerHourly.adapter = AdaptadorForecastHourly(requireContext(), arrayHourlyForecast)
        recyclerHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        btnFechar.setOnClickListener {
            dismiss()
        }

        return dialogoView
    }
}