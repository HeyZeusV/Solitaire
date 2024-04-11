package com.heyzeusv.solitaire.ui.board.games

import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.NumberOfPiles
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.ResetFaceUpAmount
import com.heyzeusv.solitaire.util.StartingScore

data object PuttPutt : Games.GolfFamily() {
    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_putt_putt
    override val familyId: Int = R.string.games_family_golf
    override val previewId: Int = R.drawable.preview_golf
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_golf,
        stockRulesId = R.string.putt_putt_stock_rules,
        wasteRulesId = null,
        foundationRulesId = R.string.putt_putt_foundation_rules,
        tableauRulesId = R.string.putt_putt_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_PUTT_PUTT

    /**
     *  [GameRules]
     */
    override val baseDeck: List<Card> = List(52) { Card(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.Five
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.One
    override val maxScore: MaxScore = MaxScore.OneDeck
    override val autocompleteAvailable: Boolean = false
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Four
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Seven

    /**
     *  Autocomplete is not available for this game.
     */
    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean = false

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEachIndexed { index, tableau ->
            if (index < numOfTableauPiles.amount) {
                val cards = List(5) { stock.remove() }
                tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
            } else {
                tableau.reset()
            }
        }
    }

    /**
     *  Start single Foundation pile with a random Card.
     */
    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        foundationList.forEachIndexed { index, foundation ->
            if (index == 3) {
                foundation.reset(listOf(stock.remove()))
            } else {
                foundation.reset()
            }
        }
    }

    override fun canAddToTableauNonEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return false
    }

    override fun canAddToTableauEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return false
    }

    /**
     *  Checks if given [cardsToAdd] is one more or less than last card of [foundation] truePile.
     *  Allows for wrapping from King to Ace or Ace to King.
     */
    override fun canAddToFoundation(foundation: Foundation, cardsToAdd: List<Card>): Boolean {
        if (cardsToAdd.isEmpty()) return false
        if (foundation.truePile.isEmpty()) return false
        val firstCard = cardsToAdd.first()
        val lastFoundationCard = foundation.truePile.last()
        return firstCard.value == lastFoundationCard.value + 1 ||
               firstCard.value == lastFoundationCard.value - 1 ||
               (firstCard.value == 0 && lastFoundationCard.value == 12) ||
               (firstCard.value == 12 && lastFoundationCard.value == 0)
    }

    override fun gameWon(foundation: List<Foundation>): Boolean {
        // single Foundation pile should have all cards
        return foundation.first().truePile.size == 52
    }
}