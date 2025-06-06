package com.deyvidandrades.meuclima.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.ForecastDataParser
import com.deyvidandrades.meuclima.objetos.ForecastHourly
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorForecastHourly(private val context: Context, arrayList: ArrayList<ForecastHourly>) :
    RecyclerView.Adapter<AdaptadorForecastHourly.ViewHolder>() {

    private var arrayList: ArrayList<ForecastHourly> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.separador.visibility = if (position == 0) View.GONE else View.VISIBLE

        holder.tvTemperatura.text = context.getString(R.string.temperatura_formatada, item.getTemperatura())
        holder.ivClima.setImageDrawable(ForecastDataParser.getWeatherDrawable(context, item.getCodeInt(), item.isDay()))

        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val currentTime = System.currentTimeMillis()

        val time = "${dateFormat.format(Date(item.getData()))}:00"
        val currentTimeFormatted = dateFormat.format(Date(currentTime))

        holder.tvData.text = if (time == currentTimeFormatted) "Agora" else time

        holder.liInfoHolder.setOnClickListener {
            Toast.makeText(context, ForecastDataParser.getCode(context, item.getCodeInt()), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTemperatura: TextView = itemView.findViewById(R.id.tv_temperatura)
        var tvData: TextView = itemView.findViewById(R.id.tv_data)
        var ivClima: ImageView = itemView.findViewById(R.id.iv_clima)
        var separador: View = itemView.findViewById(R.id.separador)

        var liInfoHolder: LinearLayout = itemView.findViewById(R.id.li_info_holder)
    }

    init {
        this.arrayList = arrayList
    }
}