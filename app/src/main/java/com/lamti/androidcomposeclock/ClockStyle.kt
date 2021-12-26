package com.lamti.androidcomposeclock

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ClockStyle(
    val radius: Dp = 100.dp,
    val color: Color = Color.White,
    val secondsPointerColor : Color = Color.Red,
    val minutesPointerColor: Color = Color.Black,
    val hoursPointerColor: Color = Color.Black,
    val secondsPointerLength: Dp = 30.dp,
    val minutesPointerLength: Dp = 45.dp,
    val hoursPointerLength: Dp = 60.dp,
    val timeLinesColor: Color = Color.LightGray,
    val stepFiveTimeLinesColor: Color = Color.DarkGray,
    val timeLinesLength: Dp = 8.dp,
    val stepFiveTimeLinesLength: Dp = 15.dp
)
