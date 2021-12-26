package com.lamti.androidcomposeclock

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TicTacToe(modifier: Modifier = Modifier, boxWidth: Float) {
    val scope = rememberCoroutineScope()
    var playersTurn by remember { mutableStateOf(Turn.Player) }
    var win: Turn? by remember { mutableStateOf(null) }
    var gameState by remember { mutableStateOf(initialState()) }
    var animations = remember { emptyAnimations() }

    val topLefOffset = Offset(0f, 0f)
    val topCenterOffset = Offset(boxWidth + 35, 0f)
    val topRightOffset = Offset(2 * boxWidth + 35, 0f)
    val centerLeftOffset = Offset(0f, boxWidth + 35)
    val centerOffset = Offset(boxWidth + 35, boxWidth + 35)
    val centerRightOffset = Offset(2 * boxWidth + 35, boxWidth + 35)
    val bottomLeftOffset = Offset(0f, 2 * boxWidth + 35)
    val bottomCenterOffset = Offset(boxWidth + 35, 2 * boxWidth + 35)
    val bottomRightOffset = Offset(2 * boxWidth + 35, 2 * boxWidth + 35)

    val topLeftRect = Rect(offset = topLefOffset, size = Size(boxWidth, boxWidth))
    val topCenterRect = Rect(offset = topCenterOffset, size = Size(boxWidth, boxWidth))
    val topRightRect = Rect(offset = topRightOffset, size = Size(boxWidth, boxWidth))
    val centerLeftRect = Rect(offset = centerLeftOffset, size = Size(boxWidth, boxWidth))
    val centerRect = Rect(offset = centerOffset, size = Size(boxWidth, boxWidth))
    val centerRightRect = Rect(offset = centerRightOffset, size = Size(boxWidth, boxWidth))
    val bottomLeftRect = Rect(offset = bottomLeftOffset, size = Size(boxWidth, boxWidth))
    val bottomCenterRect = Rect(offset = bottomCenterOffset, size = Size(boxWidth, boxWidth))
    val bottomRightRect = Rect(offset = bottomRightOffset, size = Size(boxWidth, boxWidth))

    Canvas(
        modifier = modifier.pointerInput(true) {
            detectTapGestures {
                gameState = when {
                    topLeftRect.contains(it) -> updateGame(0, gameState, scope, animations, playersTurn)
                    topCenterRect.contains(it) -> updateGame(1, gameState, scope, animations, playersTurn)
                    topRightRect.contains(it) -> updateGame(2, gameState, scope, animations, playersTurn)
                    centerLeftRect.contains(it) -> updateGame(3, gameState, scope, animations, playersTurn)
                    centerRect.contains(it) -> updateGame(4, gameState, scope, animations, playersTurn)
                    centerRightRect.contains(it) -> updateGame(5, gameState, scope, animations, playersTurn)
                    bottomLeftRect.contains(it) -> updateGame(6, gameState, scope, animations, playersTurn)
                    bottomCenterRect.contains(it) -> updateGame(7, gameState, scope, animations, playersTurn)
                    bottomRightRect.contains(it) -> updateGame(8, gameState, scope, animations, playersTurn)
                    else -> gameState
                }
                if (Rect(Offset.Zero, size.toSize()).contains(it)) {
                    playersTurn = if (playersTurn == Turn.Player) Turn.Opponent else Turn.Player
                }
                if (gameState.map { box -> box.turn }.none { box -> box == null }) {
                    scope.launch {
                        delay(3000)
                        gameState = initialState()
                        animations = emptyAnimations()
                    }
                }

                val markedList = gameState.map { box -> box.turn }
                if (
                    markedList[0] != null && markedList[0] == markedList[1] && markedList[1] == markedList[2] ||
                    markedList[3] != null && markedList[3] == markedList[4] && markedList[4] == markedList[5] ||
                    markedList[6] != null && markedList[6] == markedList[7] && markedList[7] == markedList[8] ||
                    markedList[0] != null && markedList[0] == markedList[3] && markedList[3] == markedList[6] ||
                    markedList[1] != null && markedList[1] == markedList[4] && markedList[4] == markedList[7] ||
                    markedList[2] != null && markedList[2] == markedList[5] && markedList[5] == markedList[8] ||
                    markedList[0] != null && markedList[0] == markedList[4] && markedList[4] == markedList[8] ||
                    markedList[2] != null && markedList[2] == markedList[4] && markedList[4] == markedList[6]
                ) {
                    win = playersTurn
                    scope.launch {
                        delay(3000)
                        animations = emptyAnimations()
                        gameState = initialState()
                        win = null
                    }
                }
            }
        }
    ) {
        val width = this.size.width
        drawGameLines(width)

        gameState.forEachIndexed { index, box ->
            val (offset, pathPortion) = when (index) {
                0 -> Pair(topLefOffset, animations[0])
                1 -> Pair(topCenterOffset, animations[1])
                2 -> Pair(topRightOffset, animations[2])
                3 -> Pair(centerLeftOffset, animations[3])
                4 -> Pair(centerOffset, animations[4])
                5 -> Pair(centerRightOffset, animations[5])
                6 -> Pair(bottomLeftOffset, animations[6])
                7 -> Pair(bottomCenterOffset, animations[7])
                else -> Pair(bottomRightOffset, animations[8])
            }
            drawMark(box.turn, boxWidth, offset, pathPortion)
        }

        if (win != null) {
            val message = if (win == Turn.Player) "Opponent won" else "Player won"
            drawWinningMessage(message)
        }
    }
}

private fun DrawScope.drawWinningMessage(message: String) {
    val path: android.graphics.Path = android.graphics.Path().apply {
        moveTo(0f, 800f)
        quadTo(size.width / 2f, 300f, size.width, 800f)
        offset(0f,-750f)
    }


    drawContext.canvas.nativeCanvas.apply {
        drawTextOnPath(
            message,
            path,
            0f,
            -60f,
            Paint().apply {
                color = android.graphics.Color.RED
                textSize = 130f
                textAlign = Paint.Align.CENTER
            }
        )
    }
    drawPath(
        path = path.asComposePath(),
        color = Color.Green,
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(50f, 30f)
            )
        )
    )
}

private fun DrawScope.drawMark(
    turn: Turn?,
    boxWidth: Float,
    offset: Offset,
    pathPortion: Animatable<Float, AnimationVector1D>
) {
    when (turn) {
        Turn.Player -> drawXMark(offset, boxWidth, pathPortion)
        Turn.Opponent -> drawCircle(offset, boxWidth, pathPortion)
        null -> Unit
    }
}

private fun DrawScope.drawXMark(offset: Offset, boxWidth: Float, pathPortion: Animatable<Float, AnimationVector1D>) {
    val padding = 100f
    val leftLine = Path().apply {
        moveTo(offset.x + padding, offset.y + padding)
        lineTo(offset.x + boxWidth - padding, offset.y + boxWidth - padding)
    }
    val rightLine = Path().apply {
        moveTo(offset.x + boxWidth - padding, offset.y + padding)
        lineTo(offset.x + padding, offset.y + boxWidth - padding)
    }

    drawPath(
        path = animatedPath(leftLine, pathPortion),
        color = Color.Red,
        style = Stroke(
            width = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
    drawPath(
        path = animatedPath(rightLine, pathPortion),
        color = Color.Red,
        style = Stroke(
            width = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
}

fun DrawScope.drawCircle(offset: Offset, boxWidth: Float, pathPortion: Animatable<Float, AnimationVector1D>) {
    val padding = 100f
    val circle = Path().apply {
        moveTo(boxWidth, boxWidth)
        addOval(Rect(Offset(offset.x + 60, offset.y + 60), Size(boxWidth - padding, boxWidth - padding)))
    }
    drawPath(
        path = animatedPath(circle, pathPortion),
        color = Color.Green,
        style = Stroke(
            width = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
}

private fun animatedPath(
    path: Path,
    pathPortion: Animatable<Float, AnimationVector1D>
): Path {
    val animatedPath = Path()
    PathMeasure().apply {
        setPath(path, false)
        getSegment(0f, pathPortion.value * length, animatedPath, true)
    }
    return animatedPath
}

private fun DrawScope.drawGameLines(width: Float) {
    val firstVerticalLine = Path().apply {
        moveTo(0f, width / 3)
        lineTo(width, width / 3)
    }
    val secondVerticalLine = Path().apply {
        moveTo(0f, width * 2 / 3)
        lineTo(width, width * 2 / 3)
    }
    val firstHorizontalLine = Path().apply {
        moveTo(width / 3, 0f)
        lineTo(width / 3, width)
    }
    val secondHorizontalLine = Path().apply {
        moveTo(width * 2 / 3, 0f)
        lineTo(width * 2 / 3, width)
    }

    drawGameLine(secondVerticalLine)
    drawGameLine(firstVerticalLine)
    drawGameLine(firstHorizontalLine)
    drawGameLine(secondHorizontalLine)
}

private fun DrawScope.drawGameLine(path: Path) {
    drawPath(
        path = path,
        color = Color.DarkGray,
        style = Stroke(
            width = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
}


private fun initialState() = listOf(
    Box(null),
    Box(null),
    Box(null),
    Box(null),
    Box(null),
    Box(null),
    Box(null),
    Box(null),
    Box(null),
)

private fun updateGame(
    index: Int,
    gameState: List<Box>,
    scope: CoroutineScope,
    animations: ArrayList<Animatable<Float, AnimationVector1D>>,
    playersTurn: Turn
) = if (gameState[index].turn == null) {
    scope.animateFloatToOne(animations[index])
    updateGameState(gameState, index, playersTurn)
} else gameState

private fun updateGameState(gameState: List<Box>, index: Int, turn: Turn): List<Box> {
    val mutableList = gameState.toMutableList()
    mutableList[index] = mutableList[index].copy(turn = turn)
    return mutableList
}

private fun emptyAnimations(): ArrayList<Animatable<Float, AnimationVector1D>> {
    val arrayList = ArrayList<Animatable<Float, AnimationVector1D>>()
    repeat(10) {
        arrayList.add(Animatable(0f))
    }
    return arrayList
}

private fun CoroutineScope.animateFloatToOne(animatable: Animatable<Float, AnimationVector1D>) {
    launch {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500
            )
        )
    }
}
