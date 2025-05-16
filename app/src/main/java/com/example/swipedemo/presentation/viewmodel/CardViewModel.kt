package com.example.swipedemo.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.random.Random

private fun getRandomColor(): Color {
    return Color(
        red = Random.nextFloat(),
        green = Random.nextFloat(),
        blue = Random.nextFloat(),
        alpha = 1f
    )
}

data class CardData(
    val id: Int,
    val color: Color = getRandomColor()
)

class CardViewModel : ViewModel() {
    private val _cards = mutableStateListOf<CardData>()
    val cards: List<CardData> = _cards

    init {
        loadInitialCards()
    }

    private fun loadInitialCards() {
        _cards.addAll(
            listOf(
                CardData(1),
                CardData(2),
                CardData(3),
                CardData(4),
                CardData(5)
            )
        )
    }

    fun removeTopCard() {
        if (_cards.isNotEmpty()) {
            _cards.removeAt(_cards.size - 1)
        }
    }
    fun reset() {
        _cards.addAll(
            listOf(
                CardData(1),
                CardData(2),
                CardData(3),
                CardData(4),
                CardData(5)
            )
        )
    }

}
