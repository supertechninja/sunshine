package com.mcwilliams.sunshine.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.LottieAnimationState
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.mcwilliams.sunshine.R
import com.mcwilliams.sunshine.theme.LetsComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
            CircularProgressIndicator(color = Color(0xFF1B84A0))
        }
    } else {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                if (!loading!!) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(color = Color(0xFF1B84A0))
                    ) {
                        Text(
                            text = "Daily",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp, top = 16.dp)
                        )
                        weatherData?.let { weatherData ->
                            LazyColumn(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp
                                )
                            ) {
                                items(weatherData.daily) { daily ->
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
                }
            },
            sheetPeekHeight = 220.dp,
            sheetElevation = 16.dp,
            sheetShape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
            )
        ) {
            WeatherContainer(viewModel = viewModel)
        }
    }

}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun WeatherContainer(
    viewModel: WeatherViewModel,
) {
    val weatherData by viewModel.weatherData.observeAsState()
    val loading by viewModel.loading.observeAsState()

    if (loading!!) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .background(
                    color = Color(0xFF016a86)
                )
        ) {
            val appBarHeight = 60.dp
            val appBarPadding = 1.dp

            var isSearchEnabled by remember { mutableStateOf(false) }

            val textFocusRequester = FocusRequester()
            val bodyFocusRequester = FocusRequester()

            Surface(
                color = Color(0xFF004f6d),
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
                        targetValue = if (isSearchEnabled) Color.White else Color(
                            0xFF004f6d
                        ),
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

            weatherData!!.current.let { currentWeather ->
                val focusRequestModifier = Modifier.focusRequester(bodyFocusRequester)
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF004f6d),
                                    Color(0xFF016a86)
                                )
                            )
                        )
                        .padding(bottom = 24.dp, top = 34.dp)
                        .wrapContentHeight()
                        .then(focusRequestModifier)
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
                            Instant.ofEpochSecond(weatherData!!.current.sunrise),
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
                            Instant.ofEpochSecond(weatherData!!.current.sunset),
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
                items(weatherData!!.hourly.subList(1, 12)) { hour ->
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

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun getWeatherAsset(weatherId: Int): String {
    when (weatherId) {
        in 200..299 -> {
            return "thunderstorm.json"
        }
        800 -> {
            return "sun.json"
        }
        in 801..804 -> {
            return "cloudy.json"
        }
        in 600..699 -> {
            return "snow.json"
        }
        in 500..599 -> {
            return "rain.json"
        }
        in 300..399 -> {
            return "light-rain.json"
        }
        else -> return ""
    }

}

@Composable
fun AnimatedIcon(assetName: String, animationState: LottieAnimationState, size: Dp) {
    val animationSpec = remember { LottieAnimationSpec.Asset(assetName) }
    LottieAnimation(
        spec = animationSpec,
        animationState = animationState,
        modifier = Modifier
            .requiredSize(size)
            .padding(8.dp)
    )
}