package com.heyzeusv.solitaire.board.piles

import java.util.Random
import com.heyzeusv.solitaire.board.GameViewModel

/**
 *  Provided by Hilt to [GameViewModel] which holds the [Random] obj that determines the deck
 *  shuffle.
 */
data class ShuffleSeed(val shuffleSeed: Random)