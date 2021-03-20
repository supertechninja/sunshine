package com.mcwilliams.sunshine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.mcwilliams.sunshine.model.allweatherdata.Daily
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DailyForecast(daily: List<Daily>) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.secondary)
    ) {
        Text(
            text = "Daily",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp, top = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        ) {
            items(daily) { daily ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    val date = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(daily.dt),
                        TimeZone.getDefault().toZoneId()
                    )
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (
                            weatherDate, high,
                            low, weatherDesc,
                            weatherIcon,
                        ) = createRefs()

                        val animationState = rememberLottieAnimationState(
                            autoPlay = true,
                            repeatCount = 3
                        )

                        Box(modifier = Modifier
                            .size(90.dp)
                            .constrainAs(weatherIcon) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }) {

                            AnimatedIcon(
                                assetName = getWeatherAsset(daily.weather[0].id),
                                animationState = animationState,
                                size = 90.dp
                            )
                        }

                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                            modifier = Modifier.constrainAs(weatherDate) {
                                start.linkTo(weatherIcon.end, 8.dp)
                                top.linkTo(weatherIcon.top, 24.dp)
                            }
                        )

                        Text(
                            text = daily.weather[0].description.capitalize(),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.constrainAs(weatherDesc) {
                                start.linkTo(weatherIcon.end, 8.dp)
                                top.linkTo(weatherDate.bottom)
                            }
                        )
                        Text(
                            text = "${daily.temp.max.toInt()}°",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.constrainAs(high) {
                                top.linkTo(weatherDate.top)
                                end.linkTo(parent.end, 8.dp)
                            }
                        )
                        Text(
                            text = "${daily.temp.min.toInt()}°",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .constrainAs(low) {
                                    top.linkTo(high.bottom)
                                    end.linkTo(parent.end, 8.dp)
                                }
                        )
                    }
                }
            }
        }
    }
}