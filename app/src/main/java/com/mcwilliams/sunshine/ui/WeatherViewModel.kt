package com.mcwilliams.sunshine.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.sunshine.model.allweatherdata.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val currentWeatherRepository: WeatherRepository
) : ViewModel() {

    var _weatherData = MutableLiveData<WeatherData>()
    var weatherData: LiveData<WeatherData> = _weatherData

    var _loading = MutableLiveData(true)
    val loading = _loading

    var currentCity = "Boerne"

    init {
        getWeatherData(currentCity)
    }

    fun search(text: String) {
        viewModelScope.launch {
            getWeatherData(text)
        }
    }

    fun getWeatherData(search: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            _weatherData.postValue(
                currentWeatherRepository.getWeather(search = search)
            )
            currentCity = search
            _loading.postValue(false)
        }
    }

    fun refresh() {
        getWeatherData(currentCity)
    }

}