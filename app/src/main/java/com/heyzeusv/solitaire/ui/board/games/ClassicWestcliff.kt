package com.heyzeusv.solitaire.ui.board.games

import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.ResetFaceUpAmount
import com.heyzeusv.solitaire.util.StartingScore
import com.heyzeusv.solitaire.util.Suits

data object ClassicWestcliff : Games.KlondikeFamily() {
    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_classic_westcliff
    override val familyId: Int = R.string.games_family_klondike
    override val previewId: Int = R.drawable.preview_classic_westcliff
    override val dataStoreEnum: Game = Game.GAME_CLASSIC_WESTCLIFF

    /**
     *  [GameRules]
     */
    override val baseDeck: List<Card> = List(48) { Card((it % 12) + 1, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.Four
    override val maxScore: MaxScore = MaxScore.OneDeck

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.faceDownExists()) return false }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(3) { stock.remove() }
            tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    /**
     *  Start each pile with its Ace.
     */
    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        foundationList.forEach { it.reset(listOf(Card(0, it.suit, true))) }
    }

    override fun canAddToTableauNonEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit.color != tLast.suit.color && cFirst.value == tLast.value - 1
    }

    override fun canAddToTableauEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean = true

    override fun gameWon(foundation: List<Foundation>): Boolean {
        // each foundation should have Ace to King which is 13 cards
        foundation.forEach { if (it.truePile.size != 13) return false }
        return true
    }

    override fun getSuit(i: Int) = when (i / 12) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }
}