### Sunshine ###

Simple Weather app using latest Android Jetpack Libraries. MVVM, Jetpack Compose, Kotlin Coroutines, Dagger Hilt & Retrofit

### Data ###

This app uses 2 separate public api that you will need to sign up and create your own apiKeys for:

[Open Cage Data](https://opencagedata.com/api) 

Put your api key in LocationApi.kt
```kotlin
suspend fun getLatLongByCity(
        @Query("q") city: String,
        @Query("key") key: String = "{yourApiKeyHere}"
    ): CityDataByLatLong
```

[Open Weather Api](https://openweathermap.org/appid)

Put your api key in WeatherApi.kt
```kotlin
val apiKey = "{yourApiKeyHere}"
```


### Demo ###
![Sunshine Demo](demo.gif)