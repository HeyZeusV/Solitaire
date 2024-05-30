package com.heyzeusv.solitaire.board.animation

import com.heyzeusv.solitaire.board.piles.CardLogic
import com.heyzeusv.solitaire.board.piles.Tableau

/**
 *  Contains data needed to animate a [Tableau] pile that has its last [CardLogic] revealed.
 *
 *  @property flipCard The last [CardLogic] which is either revealed or hidden.
 *  @property flipCardInfo Determines which way [flipCard] is to be animated.
 *  @property remainingPile The rest of the [Tableau] pile which is not animated, but is displayed
 *  for the duration of the animation.
 */
data class TableauCardFlipInfo(
    val flipCard: CardLogic,
    val flipCardInfo: FlipCardInfo,
    val remainingPile: List<CardLogic>,
)