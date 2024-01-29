package com.heyzeusv.solitaire

interface Pile {

    val pile: List<Card>

    fun add(cards: List<Card>): Boolean
    fun remove(tappedIndex: Int = 1): Card
    fun reset(cards: List<Card> = emptyList())
    fun undo(cards: List<Card>)
}