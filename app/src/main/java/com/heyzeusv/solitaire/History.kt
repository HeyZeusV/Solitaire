package com.heyzeusv.solitaire

/**
 *  Data class to hold [score] and list of [Card]s for each available pile. This will be used to
 *  return the user to a previous state of the game.
 */
data class History(
    val score: Int,
    val stock: Stock,
    val waste: Waste,
    val foundation: List<Foundation>,
    val tableau: List<Tableau>
)