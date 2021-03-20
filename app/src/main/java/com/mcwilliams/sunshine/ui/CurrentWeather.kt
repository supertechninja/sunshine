package com.mcwilliams.sunshine.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.mcwilliams.sunshine.R
import com.mcwilliams.sunshine.model.allweatherdata.Current
import com.mcwilliams.sunshine.model.allweatherdata.Hourly
import com.mcwilliams.sunshine.theme.lighterBlue
import com.mcwilliams.sunshine.theme.skyBlue
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@ExperimentalComposeUiApi
@Composable
fun CurrentWeather(viewModel: WeatherViewModel) {

    val weatherData by viewModel.weatherData.observeAsState()

    CurrentLocationAppBar(viewModel)

    val refreshingState by remember { mutableStateOf(false) }
    SwipeToRefreshLayout(
        refreshingState = refreshingState,
        onRefresh = {
            viewModel.refresh()
        },
        refreshIndicator = {
            Surface(elevation = 10.dp, shape = CircleShape, modifier = Modifier.padding(8.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(8.dp),
                    color = Color.White
                )
            }
        }
    ) {
        Column {
            weatherData!!.current.let { currentWeather ->
                CurrentWeatherDetails(currentWeather)
                HourlyWeather(weatherData!!.hourly.subList(1, 12))
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun CurrentLocationAppBar(viewModel: WeatherViewModel) {
    val appBarHeight = 60.dp
    val appBarPadding = 1.dp

    var isSearchEnabled by remember { mutableStateOf(false) }

    val textFocusRequester = FocusRequester()

    Surface(
        color = MaterialTheme.colors.primary,
        elevation = 4.dp,
        shape = RectangleShape,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = appBarPadding, end = appBarPadding)
                .requiredHeight(appBarHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            var textValue by remember { mutableStateOf(TextFieldValue(viewModel.currentCity)) }

            val focusModifier = Modifier.focusModifier()

            val textColor by animateColorAsState(
                targetValue = if (isSearchEnabled) Color.Black else Color.White
            )

            val textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = textColor
            )

            val color by animateColorAsState(
                targetValue = if (isSearchEnabled) Color.White else MaterialTheme.colors.primary,
            )

            Surface(
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(8.dp, 1.dp)
                    .align(Alignment.CenterVertically),
                color = color,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val focusRequestModifier =
                        Modifier.focusRequester(textFocusRequester)
                    BasicTextField(
                        value = textValue,
                        modifier = focusModifier
                            .padding(20.dp, 1.dp)
                            .fillMaxWidth(7 / 8f)
                            .align(Alignment.CenterVertically)
                            .then(focusRequestModifier),
                        onValueChange = { textValue = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        textStyle = textStyle,
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.getWeatherData(textValue.text)
                        })
                    )

                    val keyboardController = LocalSoftwareKeyboardController.current
                    IconButton(
                        onClick = {
                            isSearchEnabled = !isSearchEnabled
                            if (isSearchEnabled) {
                                textFocusRequester.requestFocus()
                                keyboardController?.showSoftwareKeyboard()
                            } else {
                                textFocusRequester.freeFocus()
                                keyboardController?.hideSoftwareKeyboard()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val tint by animateColorAsState(
                            targetValue = if (isSearchEnabled) Color.Black else Color.White
                        )

                        Icon(
                            imageVector = if (isSearchEnabled) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "",
                            tint = tint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherDetails(currentWeather: Current) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        skyBlue,
                        lighterBlue
                    )
                )
            )
            .padding(bottom = 24.dp, top = 34.dp)
            .wrapContentHeight()
    ) {
        val (
            currentDate, currentTemp,
            feelsLike, currentImage,
            currentDesc, sunriseSunset,
        ) = createRefs()

        val centerPoint = createGuidelineFromStart(0.45f)

        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM dd, hh:mm a")
        val formatDateTime: String = LocalDateTime
            .now()
            .format(formatter)
        Text(
            text = formatDateTime,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.constrainAs(currentDate) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = "${currentWeather.temp.toInt()}°",
            style = MaterialTheme.typography.h2,
            modifier = Modifier.constrainAs(currentTemp) {
                top.linkTo(currentDate.bottom, 16.dp)
                start.linkTo(centerPoint)
            }
        )
        Text(
            text = "Feels Like ${currentWeather.feels_like.toInt()}°F",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .constrainAs(feelsLike) {
                    top.linkTo(currentDesc.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(modifier = Modifier
            .requiredSize(120.dp)
            .constrainAs(currentImage) {
                top.linkTo(currentDate.bottom, 16.dp)
                bottom.linkTo(currentTemp.bottom)
                end.linkTo(centerPoint)
            }) {
            val animationState = rememberLottieAnimationState(
                autoPlay = true,
                repeatCount = Integer.MAX_VALUE
            )

            AnimatedIcon(
                assetName = getWeatherAsset(currentWeather.weather[0].id),
                animationState = animationState,
                size = 120.dp
            )
        }

        Text(
            text = currentWeather.weather[0].description.capitalize(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.constrainAs(currentDesc) {
                top.linkTo(currentTemp.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Row(modifier = Modifier.constrainAs(sunriseSunset) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(feelsLike.bottom, 16.dp)
        }) {
            val sunrise = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(currentWeather.sunrise),
                TimeZone
                    .getDefault()
                    .toZoneId()
            )

            Image(
                painter = painterResource(id = R.drawable.ic_sunrise),
                contentDescription = "",
                modifier = Modifier.requiredSize(28.dp),
                colorFilter = ColorFilter.tint(Color(0xFFDCC60D))
            )
            Text(
                text = sunrise.format(DateTimeFormatter.ofPattern("hh:mm a")),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )

            val sunset = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(currentWeather.sunset),
                TimeZone
                    .getDefault()
                    .toZoneId()
            )

            Image(
                painter = painterResource(id = R.drawable.ic_sunset),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredSize(28.dp),
                colorFilter = ColorFilter.tint(Color(0xFFDCC60D))
            )
            Text(text = sunset.format(DateTimeFormatter.ofPattern("hh:mm a")))
        }
    }
}

@Composable
fun HourlyWeather(hourly: List<Hourly>) {
    Text(
        text = "Hourly",
        style = MaterialTheme.typography.h5,
        modifier = Modifier.padding(start = 24.dp, top = 8.dp)
    )

    LazyRow(
        modifier = Modifier
            .padding(top = 8.dp, start = 24.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        items(hourly) { hour ->
            val localTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(hour.dt),
                TimeZone.getDefault().toZoneId()
            )
            Card(
                elevation = 4.dp,
                shape = RoundedCornerShape(25),
                backgroundColor = Color.White,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(4.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {

                    val (
                        weatherIcon, temp,
                        time
                    ) = createRefs()


                    val animationState = rememberLottieAnimationState(
                        autoPlay = true,
                        repeatCount = 1
                    )

                    Box(modifier = Modifier
                        .size(60.dp)
                        .constrainAs(weatherIcon) {
                            top.linkTo(parent.top, 8.dp)
                            start.linkTo(parent.start, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                        }) {

                        AnimatedIcon(
                            assetName = getWeatherAsset(hour.weather[0].id),
                            animationState = animationState,
                            size = 60.dp
                        )
                    }

                    Text(
                        text = "${hour.temp.toInt()}°F",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = Color.Black,
                        modifier = Modifier.constrainAs(temp) {
                            top.linkTo(weatherIcon.bottom, 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                    Text(
                        text = localTime.format(DateTimeFormatter.ofPattern("hh a")),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .constrainAs(time) {
                                top.linkTo(temp.bottom, 8.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.caption,
                        color = Color.Black
                    )
                }
            }
        }
    }
}