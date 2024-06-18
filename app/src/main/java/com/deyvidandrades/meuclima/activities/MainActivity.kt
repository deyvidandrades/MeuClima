package com.deyvidandrades.meuclima.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deyvidandrades.meuclima.R
import com.deyvidandrades.meuclima.assistentes.DWS
import com.deyvidandrades.meuclima.assistentes.ForecastDataParser
import com.deyvidandrades.meuclima.assistentes.NotificacoesUtil
import com.deyvidandrades.meuclima.assistentes.Persistencia
import com.deyvidandrades.meuclima.assistentes.RequestManager
import com.deyvidandrades.meuclima.assistentes.WorkManagerUtil
import com.deyvidandrades.meuclima.dialogos.DialogoConfiguracoes
import com.deyvidandrades.meuclima.dialogos.DialogoForecast
import com.deyvidandrades.meuclima.objetos.ForecastDaily
import com.deyvidandrades.meuclima.objetos.ForecastHourly
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var localizacao: Pair<Double, Double>? = null

    private var arrayHorasDia = ArrayList<ForecastHourly>()
    private var arrayProximosDias = ArrayList<ForecastDaily>()

    private lateinit var tvTemperatura: TextView
    private lateinit var tvData: TextView
    private lateinit var tvDescricao: TextView
    private lateinit var tvPrecipitacao: TextView
    private lateinit var tvHumidade: TextView
    private lateinit var tvVento: TextView
    private lateinit var tvCidade: TextView
    private lateinit var ivClima: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Persistencia.getInstance(this)

        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh)
        val btnOpcoes: Button = findViewById(R.id.btn_opcoes)
        val btnForecast: Button = findViewById(R.id.btn_forecast)

        val switchTemaEscuro: MaterialSwitch = findViewById(R.id.btn_mudar_tema)

        tvTemperatura = findViewById(R.id.tv_temperatura)
        tvData = findViewById(R.id.tv_data)
        tvDescricao = findViewById(R.id.tv_descricao)
        tvPrecipitacao = findViewById(R.id.tv_precipitacao)
        tvHumidade = findViewById(R.id.tv_humidade)
        tvCidade = findViewById(R.id.tv_cidade)
        tvVento = findViewById(R.id.tv_vento)
        ivClima = findViewById(R.id.iv_clima)

        switchTemaEscuro.isChecked = Persistencia.isDarkTheme
        switchTemaEscuro.setOnCheckedChangeListener { _, _ ->
            Persistencia.setDarkTheme()
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
            finish()
        }

        swipeRefreshLayout.setOnRefreshListener {
            getLocalizacao()
            swipeRefreshLayout.isRefreshing = false
        }

        btnForecast.setOnClickListener {
            val customBottomSheet = DialogoForecast()
            customBottomSheet.arrayDailyForecast = arrayProximosDias
            customBottomSheet.arrayHourlyForecast = arrayHorasDia
            customBottomSheet.show(
                supportFragmentManager,
                DialogoForecast().javaClass.name
            )
        }

        btnOpcoes.setOnClickListener {
            val customBottomSheet = DialogoConfiguracoes()
            customBottomSheet.show(
                supportFragmentManager,
                DialogoConfiguracoes().javaClass.name
            )
        }

        mudarTema()
        configurarPermissoes()

        WorkManagerUtil.iniciarWorker(this, WorkManagerUtil.Tipo.NOTIFICACAO)
    }

    private fun mudarTema() {
        AppCompatDelegate.setDefaultNightMode(
            if (Persistencia.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    @SuppressLint("InlinedApi")
    private fun configurarPermissoes() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permissionRequestLocalizacao =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        getLocalizacao()
                    }

                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        getLocalizacao()
                    }
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                getLocalizacao()
            }

            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                getLocalizacao()
            }

            else -> {
                permissionRequestLocalizacao.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }


        val permissionRequestNotificacao =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                        configurarNotificacoes()
                    }
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                configurarNotificacoes()
            }

            else -> {
                permissionRequestNotificacao.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocalizacao() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    atualizarUI(location.latitude, location.longitude)
                    Persistencia.setLocalizacao(location.latitude, location.longitude)
                }
            }
    }

    private fun configurarNotificacoes() {
        NotificacoesUtil.criarCanalDeNotificacoes(this)
    }

    @SuppressLint("SetTextI18n")
    private fun atualizarUI(latitude: Double, longitude: Double) {
        localizacao = Pair(latitude, longitude)

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        var cidade = ""
        if (!addresses.isNullOrEmpty() && addresses[0].subAdminArea != null)
            //cidade = "${addresses[0].subAdminArea}, ${addresses[0].adminArea}"
            cidade = addresses[0].subAdminArea

        var result: String
        runBlocking {
            result = RequestManager.fazerRequisicao(ForecastDataParser.getApiUrl(latitude, longitude))
        }

        ForecastDataParser.getForecast(result) { current, hourly, week ->
            arrayHorasDia = hourly
            arrayProximosDias = week

            val calendar = Calendar.getInstance()
            val dayOfWeek =
                SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time).replaceFirstChar { it.uppercase() }
            val hourMinute = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

            tvTemperatura.text = current.getTemperatura().toString()
            tvData.text = "$dayOfWeek, $hourMinute"
            tvDescricao.text = current.getCode()
            tvCidade.text = cidade
            tvHumidade.text = "${current.getHumidade()}%"
            tvPrecipitacao.text = "${current.getPrecipitacao()}%"
            tvVento.text = current.getVento().toString()

            ivClima.setImageDrawable(ForecastDataParser.getWeatherDrawable(this, current.getCodeInt(), current.isDia()))
        }
    }
}