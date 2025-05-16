package com.example.swipedemo.presentation.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.swipedemo.presentation.viewmodel.CardData
import com.example.swipedemo.presentation.viewmodel.CardViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CardStack(cardViewModel: CardViewModel, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val cardsToDisplay: List<CardData> = cardViewModel.cards
    Box(modifier = modifier) {
        TextButton(
            onClick = {
                cardViewModel.reset()
            }

        ) {
            Text("Reset", color = Color.Black)
        }
    }
    Box(modifier = modifier) {
        cardsToDisplay.dropLast(1).forEachIndexed { index: Int, cardData: CardData -> // Explicit types for lambda params
            CardItem(
                modifier = Modifier.offset(y = (index * 8).dp),
                color = cardData.color
            )
        }

        if (cardsToDisplay.isNotEmpty()) {
            val offsetX = remember { Animatable(0f) }
            val topCardData = cardsToDisplay.last()

            CardItem(
                color = topCardData.color,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            offsetX.value.roundToInt(),
                            -abs(offsetX.value.roundToInt()) / 3 + ((cardsToDisplay.size - 1) * 8).dp.roundToPx()
                        )
                    }
                    .rotate(45 * offsetX.value / LocalConfiguration.current.screenWidthDp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { /* Optional: Handle drag start */ },
                            onDragEnd = {
                                println(offsetX.value)
                                if (abs(offsetX.value) > 200) {
                                    coroutineScope.launch {
                                        val targetX = if (offsetX.value > 0) offsetX.value + 1000 else offsetX.value -1000
                                        offsetX.animateTo(
                                            targetX, animationSpec = tween(
                                                durationMillis = 300
                                            )
                                        )
                                        cardViewModel.removeTopCard() // Call ViewModel function
                                        offsetX.snapTo(0f) // Reset for the next card
                                    }
                                } else {
                                    coroutineScope.launch {
                                        offsetX.animateTo(
                                            0f, animationSpec = tween(
                                                durationMillis = 200
                                            )
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, animationSpec = tween(durationMillis = 200))
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                }
                            }
                        )
                    }
            )
        }
    }
}
