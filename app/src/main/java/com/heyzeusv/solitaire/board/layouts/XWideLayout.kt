package com.heyzeusv.solitaire.board.layouts

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.HorizontalCardPileWithFlip
import com.heyzeusv.solitaire.board.MultiPileCardWithFlip
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.util.GamePiles

interface XWideLayout {
    val cardConstraints: Constraints
    val wasteConstraints: Constraints

    /**
     *  This is used in any [Column] [Arrangement.spacedBy] involving [Card]s.
     */
    val vPileSpacedByPercent: Float

    /**
     *  Layout ids used by [HorizontalCardPileWithFlip]
     */
    val horizontalPileLayoutIds: List<String>

    /**
     *  Layout ids used by [MultiPileCardWithFlip].
     */
    val multiPileLayoutIds: List<String>

    /**
     *  Used by card animations to determine pile offset.
     *
     *  @param gamePile The pile offset to retrieve.
     *  @param stockWasteMove Animations between Stock and Waste requires a different offset.
     *  @param tAllPile Used to call this function recursively in order to get correct offset,
     *  when [gamePile] is [GamePiles.TableauAll].
     */
    fun getPilePosition(
        gamePile: GamePiles,
        stockWasteMove: Boolean = false,
        tAllPile: GamePiles = GamePiles.TableauZero
    ): IntOffset

    /**
     *  Card animations involving Tableau piles require Y offset to determine where card moving
     *  beings/ends.
     *
     *  @param index The index of [Card] in Tableau pile.
     *  @return The y offset of Tableau [Card], but in [IntOffset] form.
     */
    fun getCardsYOffset(index: Int): IntOffset

    /**
     *  Used by [HorizontalCardPileWithFlip] in order to retrieve necessary offsets for animations.
     *
     *  @param flipCardInfo Determines start/end positions.
     *  @return [HorizontalCardOffsets] data class containing [IntOffsets][IntOffset]
     */
    fun getHorizontalCardOffsets(flipCardInfo: FlipCardInfo): HorizontalCardOffsets

    /**
     *  @return The size of [Card] in the form of [DpSize].
     */
    @Composable
    fun getCardDpSize(): DpSize
}