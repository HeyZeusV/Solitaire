package com.heyzeusv.solitaire.board.piles

import java.util.Random

/**
 *  Used to determine deck shuffle.
 *
 *  @property shuffleSeed The [Random] obj which is used with [List.shuffled] to shuffle deck.
 */
data class ShuffleSeed(val shuffleSeed: Random)