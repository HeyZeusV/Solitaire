package com.heyzeusv.solitaire.board.animation

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.util.GamePiles

/**
 *  Contains data needed to animate one legal move. [start] is where the animation begins, while
 *  [end] is where it finishes. [animatedCards] refers to [Card]s moving from one pile to another.
 *  [startTableauIndices] and [endTableauIndices] are used to determine the Y offset needed when
 *  moving to/from a Tableau pile. [flipCardInfo] is used for flip animation for cards moving
 *  from one pile to another. [tableauCardFlipInfo] contains data needed to animate a Tableau pile
 *  that has its bottom most [Card] revealed. [undoAnimation] is used to disable all clicks on
 *  game board during an animation started by Undo Button. [spiderPile] is used to determine if
 *  [animatedCards] contains Ace to King ranked correctly and same suit as required in Spider games.
 */
data class AnimateInfo(
    val start: GamePiles,
    val end: GamePiles,
    val animatedCards: List<Card>,
    val startTableauIndices: List<Int> = List(13) { 0 },
    val endTableauIndices: List<Int> = List(13) { 0 },
    val flipCardInfo: FlipCardInfo = FlipCardInfo.NoFlip,
    val tableauCardFlipInfo: TableauCardFlipInfo? = null,
    val undoAnimation: Boolean = false,
    val spiderPile: Boolean = false
) {
    // determines if animation is between Stock and Waste
    val stockWasteMove = (start == GamePiles.Stock && end == GamePiles.Waste) ||
            (start == GamePiles.Waste && end == GamePiles.Stock)
    // functions that are assigned in GameViewModel and are ran before/after animations
    var actionBeforeAnimation: suspend () -> Unit = { }
    var actionAfterAnimation: suspend () -> Unit = { }

    /**
     *  Creates a new [AnimateInfo] from this, but with reversed values which is used by Undo.
     */
    fun getUndoAnimateInfo(): AnimateInfo = AnimateInfo(
        start = end,
        end = start,
        animatedCards = animatedCards,
        startTableauIndices = endTableauIndices,
        endTableauIndices = startTableauIndices,
        flipCardInfo = flipCardInfo.getUndoFlipCardInfo(),
        tableauCardFlipInfo = tableauCardFlipInfo?.copy(
            flipCardInfo = tableauCardFlipInfo.flipCardInfo.getUndoFlipCardInfo()
        ),
        undoAnimation = true,
        spiderPile = spiderPile
    )

    /**
     *  Used to check if animation involves all Tableau piles, which requires a separate animation
     *  compared to animations between 2 piles.
     */
    fun isNotMultiPile(): Boolean = start != GamePiles.TableauAll && end != GamePiles.TableauAll
}

/**
 *  Contains data needed to animate a Tableau pile that has its bottom most [Card] revealed.
 *  [flipCard] is the bottom most [Card] which is either revealed or hidden. [flipCardInfo] is used
 *  by flip animation to determine which way to flip. [remainingPile] refers to the rest of the
 *  Tableau pile which is not animated, but is displayed during animation.
 */
data class TableauCardFlipInfo(
    val flipCard: Card,
    val flipCardInfo: FlipCardInfo,
    val remainingPile: List<Card>
)