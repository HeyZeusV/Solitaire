package com.heyzeusv.solitaire.ui.board.layouts.positions

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.GamePiles

interface XWideLayout {
    val cardConstraints: Constraints
    val wasteConstraints: Constraints

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