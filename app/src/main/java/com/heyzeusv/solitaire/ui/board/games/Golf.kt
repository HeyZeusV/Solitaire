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

data object Golf : Games.GolfFamily() {
    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_golf
    override val familyId: Int = R.string.games_family_golf
    override val previewId: Int = R.drawable.preview_golf
    override val dataStoreEnum: Game = Game.GAME_GOLF

    /**
     *  [GameRules]
     */
    override val baseDeck: List<Card> = List(52) { Card(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.Five
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.One
    override val maxScore: MaxScore = MaxScore.OneDeck
    override val anyCardCanStartPile: Boolean = false

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean = false

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(5) { stock.remove() }
            tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    /**
     *  Start single Foundation pile with a random Card.
     */
    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        foundationList[3].reset(listOf(stock.remove()))
    }

    override fun canAddToTableauRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean = false

    /**
     *  Checks if given [cardsToAdd] is one more or less than last card of [foundation] truePile.
     */
    override fun canAddToFoundation(foundation: Foundation, cardsToAdd: List<Card>): Boolean {
        if (cardsToAdd.isEmpty()) return false
        if (foundation.truePile.isEmpty()) return false
        val firstCard = cardsToAdd[0]
        val lastFoundationCard = foundation.truePile.last()
        return firstCard.value == lastFoundationCard.value + 1 ||
               firstCard.value == lastFoundationCard.value - 1
    }
}