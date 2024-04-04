package com.deyvidandrades.meuclima.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.ForecastDataParser
import com.deyvidandrades.meuclima.interfaces.OnItemClickListener
import com.deyvidandrades.meuclima.objetos.ForecastHourly
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorForecastHourly(context: Context, arrayList: ArrayList<ForecastHourly>, listener: OnItemClickListener) :
    RecyclerView.Adapter<AdaptadorForecastHourly.ViewHolder>() {

    private val context: Context
    private val listener: OnItemClickListener
    private var arrayList: ArrayList<ForecastHourly> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.separador.visibility = if (position == 0) View.GONE else View.VISIBLE

        holder.tvTemperatura.text = "${item.getTemperatura()}º"
        holder.ivClima.setImageDrawable(ForecastDataParser.getWeatherDrawable(context, item.getCodeInt(), item.isDay()))

        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val currentTime = System.currentTimeMillis()

        val time = "${dateFormat.format(Date(item.getData()))}:00"
        val currentTimeFormatted = dateFormat.format(Date(currentTime))

        holder.tvData.text = if (time == currentTimeFormatted) "Agora" else time
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTemperatura: TextView
        var tvData: TextView
        var ivClima: ImageView
        var separador: View

        init {
            tvTemperatura = itemView.findViewById(R.id.tv_temperatura)
            tvData = itemView.findViewById(R.id.tv_data)
            ivClima = itemView.findViewById(R.id.iv_clima)
            separador = itemView.findViewById(R.id.separador)
        }
    }

    init {
        this.context = context
        this.arrayList = arrayList
        this.listener = listener
    }
}