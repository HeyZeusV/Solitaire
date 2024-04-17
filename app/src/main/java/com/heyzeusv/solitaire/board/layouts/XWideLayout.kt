package com.heyzeusv.solitaire.board.layouts

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.MultiPileCardWithFlip
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
     *  Layout ids used by [MultiPileCardWithFlip].
     */
    val multiPileLayoutIds: List<String>

    /**
     *  Used by movement animations to determine given [gamePile] offset. Animations between
     *  Stock and Waste requires a different offset, [stockWasteMove] determines which to use.
     *  [tAllPile] is used to recursively call this function in order to get the correct offset,
     *  when [gamePile] is [GamePiles.TableauAll].
     */
    fun getPilePosition(
        gamePile: GamePiles,
        stockWasteMove: Boolean = false,
        tAllPile: GamePiles = GamePiles.TableauZero
    ): IntOffset

    /**
     *  Used by animations involving Tableau piles in order to determine additional Y offset needed
     *  since animation could only involve a sublist of Tableau, rather than entire pile.
     */
    fun getCardsYOffset(index: Int): IntOffset

    /**
     *  Returns size of [Card] in dp in the form of [DpSize].
     */
    @Composable
    fun getCardDpSize(): DpSize
}