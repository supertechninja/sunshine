package com.mcwilliams.sunshine.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mcwilliams.sunshine.theme.LetsComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LetsComposeTheme {
                WeatherBottomSheetScaffold(viewModel)
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun WeatherBottomSheetScaffold(viewModel: WeatherViewModel) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val weatherData by viewModel.weatherData.observeAsState()
    val loading by viewModel.loading.observeAsState()

    if (loading!!) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }
    } else {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                if (!loading!!) {
                    weatherData?.let { weatherData ->
                        DailyForecast(daily = weatherData.daily)
                    }
                }
            },
            sheetPeekHeight = 220.dp,
            sheetElevation = 16.dp,
            sheetShape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
            )
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .scrollable(scrollState, orientation = Orientation.Vertical)
                    .background(
                        color = MaterialTheme.colors.primaryVariant
                    )
            ) {
                CurrentWeather(viewModel)
            }
        }
    }
}