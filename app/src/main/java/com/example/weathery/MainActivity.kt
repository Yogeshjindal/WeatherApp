package com.example.weathery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.SearchView
import androidx.core.view.LayoutInflaterCompat
import com.example.weathery.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



//843b7e27809a918a0d7c5521f7cbe7d5
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

            val response=retrofit.getWeatherData(cityName,"843b7e27809a918a0d7c5521f7cbe7d5","metric")
            response.enqueue(object : Callback<WeatherApp> {
                override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                    val responseBody=response.body()
                    if(response.isSuccessful && responseBody!=null){
                        val temp=responseBody.main.temp.toString()
                        val humidity=responseBody.main.humidity
                        val windSpeed=responseBody.wind.speed
                        val sunrise=responseBody.sys.sunrise.toLong()
                        val sunset=responseBody.sys.sunset.toLong()
                        val seaLevel=responseBody.main.pressure
                        val condition=responseBody.weather.firstOrNull()?.main?: "unknown"
                        val maxTemp=responseBody.main.temp_max
                        val minTemp=responseBody.main.temp_min

                        binding.temp.text="$temp °C"
                        binding.humidity.text="$humidity %"
                        binding.windspeed.text="$windSpeed m/s"
                        binding.condition.text="$condition"
                        binding.Weather.text="$condition"
                        binding.sunrise.text="${time(sunrise)}"
                        binding.sunset.text="${time(sunset)}"
                        binding.wind.text="$seaLevel"
                        binding.maxTemp.text="Max Temp: $maxTemp °C"
                        binding.minTemp.text="Min Temp: $minTemp °C"
                        binding.date.text=date()
                        binding.day.text=day(System.currentTimeMillis())
                        binding.cityName.text="$cityName"
                        changeImage(condition)
                    }

                }

                private fun changeImage(condition: String) {
                    when (condition.trim().uppercase(Locale.ROOT)) {
                        "PARTLY CLOUDY", "CLOUDY","CLOUDS", "OVERCAST", "MIST", "SMOKE", "CLEAR" -> {
                            binding.root.setBackgroundResource(R.drawable.colud_background)
                            binding.lottieAnimationView.setAnimation(R.raw.cloud)
                        }

                        "CLEAR SKY", "SUNNY", "HAZE" -> {
                            binding.root.setBackgroundResource(R.drawable.sunny_background)
                            binding.lottieAnimationView.setAnimation(R.raw.sun)
                        }

                        "LIGHT RAIN", "DRIZZLE", "MODERATE RAIN", "SHOWERS", "HEAVY RAIN" -> {
                            binding.root.setBackgroundResource(R.drawable.rain_background)
                            binding.lottieAnimationView.setAnimation(R.raw.rain)
                        }

                        "LIGHT SNOW", "MODERATE SNOW", "SNOWY", "BLIZZARD" -> {
                            binding.root.setBackgroundResource(R.drawable.snow_background)
                            binding.lottieAnimationView.setAnimation(R.raw.snow)
                        }

                        // Add more conditions if needed

                        else -> {
                            // Default case, handle unknown conditions
                            // You can set a default background or animation here
                        }
                    }
                    binding.lottieAnimationView.playAnimation()
                }
                private fun date(): String {
                    val sdf=SimpleDateFormat("dd MM yyyy", Locale.getDefault())
                    return sdf.format((Date()))
                }

                private fun time(time: Long): String {
                    val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
                    return sdf.format((Date(time*1000)))
                }
                private fun day(currentTimeMillis: Long): String {
                    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
                    return sdf.format(Date(currentTimeMillis))
                }

                override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

                }

            })

    }


}