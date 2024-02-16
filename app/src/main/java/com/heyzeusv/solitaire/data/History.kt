package com.heyzeusv.solitaire.data

/**
 *  Data class to hold list of [Card]s for each available pile. This will be used to return the user
 *  to a previous state of the game.
 */
data class PileHistory(
    val stock: Stock,
    val waste: Waste,
    val foundation: List<Foundation>,
    val tableau: List<TableauPile>
)

/**
 *  Data class to hold [score]. This will be used to return the user to a previous state of the game.
 */
data class ScoreHistory(val score: Int)