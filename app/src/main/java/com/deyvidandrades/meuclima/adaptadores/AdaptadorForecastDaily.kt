package com.deyvidandrades.meuclima.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.ForecastDataParser
import com.deyvidandrades.meuclima.objetos.ForecastDaily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorForecastDaily(private val context: Context, arrayList: ArrayList<ForecastDaily>) :
    RecyclerView.Adapter<AdaptadorForecastDaily.ViewHolder>() {

    private var arrayList: ArrayList<ForecastDaily> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_daily, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.separador.visibility = if (position == 0) View.GONE else View.VISIBLE

        holder.tvTemperaturaMax.text = context.getString(R.string.temperatura_formatada, item.getTemperaturaMax())
        holder.tvTemperaturaMin.text = context.getString(R.string.temperatura_formatada, item.getTemperaturaMin())

        holder.ivClima.setImageDrawable(ForecastDataParser.getWeatherDrawable(context, item.getCodeInt(), true))

        holder.tvData.text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(item.getData()))
        holder.tvDia.text = SimpleDateFormat("EE", Locale.getDefault()).format(Date(item.getData()))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvData: TextView = itemView.findViewById(R.id.tv_data)
        var tvTemperaturaMax: TextView = itemView.findViewById(R.id.tv_temperatura_max)
        var tvTemperaturaMin: TextView = itemView.findViewById(R.id.tv_temperatura_min)
        var tvDia: TextView = itemView.findViewById(R.id.tv_dia)
        var ivClima: ImageView = itemView.findViewById(R.id.iv_clima)
        var separador: View = itemView.findViewById(R.id.separador)
    }

    init {
        this.arrayList = arrayList
    }
}