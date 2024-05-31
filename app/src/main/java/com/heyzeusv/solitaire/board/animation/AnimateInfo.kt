package com.heyzeusv.solitaire.board.animation

import com.heyzeusv.solitaire.board.Board
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles

/**
 *  Contains all the data needed to animate one legal move.
 *
 *  @property start The pile where animation beings.
 *  @property end The pile where animation ends.
 *  @property animatedCards The [Cards][Card] moving from [start] pile to [end] pile.
 *  @property startTableauIndices Determines Y offset when starting from a [Tableau] pile.
 *  @property endTableauIndices Determines Y offset when ending at a [Tableau] pile.
 *  @property flipCardInfo Determines if [animatedCards] need to be flipped either face up or down.
 *  @property tableauCardFlipInfo Determines if the last [Card] of a [Tableau] pile needs to be
 *  flipped.
 *  @property isUndoAnimation Used to disable all clicks on [Board] during undo animation.
 *  @property isSpiderPile Determines if [animatedCards] contains a full valid pile as required by
 *  Spider.
 */
data class AnimateInfo(
    val start: GamePiles,
    val end: GamePiles,
    val animatedCards: List<Card>,
    val startTableauIndices: List<Int> = List(13) { 0 },
    val endTableauIndices: List<Int> = List(13) { 0 },
    val flipCardInfo: FlipCardInfo = FlipCardInfo.NoFlip,
    val tableauCardFlipInfo: TableauCardFlipInfo? = null,
    val isUndoAnimation: Boolean = false,
    val isSpiderPile: Boolean = false,
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
        isUndoAnimation = true,
        isSpiderPile = isSpiderPile
    )

    /**
     *  Used to check if animation involves all Tableau piles, which requires a separate animation
     *  compared to animations between 2 piles.
     */
    fun isNotMultiPile(): Boolean = start != GamePiles.TableauAll && end != GamePiles.TableauAll
}