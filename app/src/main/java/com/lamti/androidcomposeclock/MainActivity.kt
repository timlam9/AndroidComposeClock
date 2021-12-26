package com.lamti.androidcomposeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lamti.androidcomposeclock.ui.theme.AndroidComposeClockTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.tan

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeClockTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            ClockScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopEnd)
                    .background(Color.White)
                    .padding(20.dp)
            )
        }
    }

    @Composable
    private fun ClockScreen(modifier: Modifier) {
        Box(modifier = modifier) {
            val milliseconds = remember { System.currentTimeMillis() }
            var seconds by remember { mutableStateOf((milliseconds / 1000f) % 60f) }
            var minutes by remember { mutableStateOf(((milliseconds / 1000f) / 60) % 60f) }
            var hours by remember { mutableStateOf((milliseconds / 1000f) / 3600f + 2f) }

            LaunchedEffect(key1 = seconds) {
                delay(1000L)
                minutes += 1f / 60f
                hours += 1f / (60f * 12f)
                seconds += 1f
            }

            Clock(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center),
                style = ClockStyle(),
                seconds = seconds,
                minutes = minutes,
                hours = hours
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidComposeClockTheme {
        Clock(
            modifier = Modifier.size(200.dp),
            style = ClockStyle()
        )
    }
}
