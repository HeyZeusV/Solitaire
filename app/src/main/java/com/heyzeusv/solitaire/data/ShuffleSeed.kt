package com.heyzeusv.solitaire.data

import java.util.Random

/**
 *  Provided by Hilt to GameViewModel which holds the Random obj that determines the deck shuffle.
 */
data class ShuffleSeed(val shuffleSeed: Random)