package com.lamti.androidcomposeclock

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock(
    modifier: Modifier,
    style: ClockStyle,
    seconds: Float = 0f,
    minutes: Float = 0f,
    hours: Float = 0f
) {
    Canvas(modifier = modifier) {
        drawClock(style)
        drawLines(style)
        drawPointer(
            length = style.secondsPointerLength.toPx(),
            color = style.secondsPointerColor,
            rotationDegrees = seconds * (360f / 60f)
        )
        drawPointer(
            length = style.minutesPointerLength.toPx(),
            color = style.minutesPointerColor,
            rotationDegrees = minutes * (360f / 60f)
        )
        drawPointer(
            length = style.hoursPointerLength.toPx(),
            color = style.hoursPointerColor,
            rotationDegrees = hours * (360f / 12f)
        )
    }
}

private fun DrawScope.drawClock(
    style: ClockStyle
) {
    this.drawContext.canvas.nativeCanvas.apply {
        drawCircle(
            center.x,
            center.y,
            style.radius.toPx(),
            Paint().apply {
                color = Color.WHITE
                setStyle(Paint.Style.FILL)
                setShadowLayer(
                    60f,
                    0f,
                    0f,
                    Color.argb(50, 0, 0, 0)
                )
            }
        )
    }
}

private fun DrawScope.drawLines(
    style: ClockStyle
) {
    repeat(360) {
        val angleInRad = it * (PI / 180f).toFloat()
        val line = when {
            it.rem(30) == 0 -> Line.FiveStepLine
            it.rem(6) == 0 -> Line.NormalLine
            else -> Line.NoLine
        }
        val lineLength = when (line) {
            Line.FiveStepLine -> style.stepFiveTimeLinesLength
            Line.NormalLine -> style.timeLinesLength
            else -> 0.dp
        }.toPx()
        val lineColor = when (line) {
            Line.FiveStepLine -> style.stepFiveTimeLinesColor
            Line.NormalLine -> style.timeLinesColor
            else -> style.timeLinesColor
        }
        val lineStart = Offset(
            x = (style.radius.toPx() - lineLength) * cos(angleInRad) + center.x,
            y = (style.radius.toPx() - lineLength) * sin(angleInRad) + center.y
        )
        val lineEnd = Offset(
            x = style.radius.toPx() * cos(angleInRad) + center.x,
            y = style.radius.toPx() * sin(angleInRad) + center.y
        )

        drawLine(
            color = lineColor,
            start = lineStart,
            end = lineEnd,
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawPointer(
    length: Float,
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float = 2.dp.toPx(),
    rotationDegrees: Float
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, length),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
