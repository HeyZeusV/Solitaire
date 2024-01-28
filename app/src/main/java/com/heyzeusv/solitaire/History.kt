package com.heyzeusv.solitaire

/**
 *  Data class to hold [score] and list of [Card]s for each available pile. This will be used to
 *  return the user to a previous state of the game.
 */
data class History(
    val score: Int,
    val stock: List<Card>,
    val waste: List<Card>,
    val foundation: List<List<Card>>,
    val tableauFaceUp: List<Int>,
    val tableau: List<List<Card>>
)