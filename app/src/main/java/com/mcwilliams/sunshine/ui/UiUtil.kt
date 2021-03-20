package com.mcwilliams.sunshine.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.LottieAnimationState

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