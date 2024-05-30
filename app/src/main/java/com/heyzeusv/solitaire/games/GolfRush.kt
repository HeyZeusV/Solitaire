package com.heyzeusv.solitaire.games

import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.CardLogic
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.NumberOfPiles
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.ResetFaceUpAmount
import com.heyzeusv.solitaire.util.StartingScore

data object GolfRush : Games.GolfFamily() {
    /**
     *  Checks if given [cardToAdd] is one more or less than last card of [foundation] truePile.
     */
    override fun canAddToFoundation(foundation: Foundation, cardToAdd: CardLogic): Boolean {
        if (foundation.truePile.isEmpty()) return false
        val lastFoundationCard = foundation.truePile.last()
        return cardToAdd.value == lastFoundationCard.value + 1 ||
                cardToAdd.value == lastFoundationCard.value - 1
    }

    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        return false
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        return false
    }

    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_golf_rush
    override val familyId: Int = R.string.games_family_golf
    override val previewId: Int = R.drawable.preview_golf_rush
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_golf_rush,
        stockRulesId = R.string.golf_rush_stock_rules,
        wasteRulesId = null,
        foundationRulesId = R.string.golf_rush_foundation_rules,
        tableauRulesId = R.string.golf_rush_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_GOLF_RUSH
    override val dbName: String = "golfRush"

    /**
     *  [GameRules]
     */
    override val baseDeck: List<CardLogic> = List(52) { CardLogic(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.Seven
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
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = List(i + 1) { stock.remove() }
            tableauList[i].reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    /**
     *  Start single Foundation pile with a random Card.
     */
    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        foundationList[3].reset(listOf(stock.remove()))
    }

    override fun gameWon(foundation: List<Foundation>): Boolean {
        // single Foundation pile should have all cards
        return foundation[3].truePile.size == 52
    }
}