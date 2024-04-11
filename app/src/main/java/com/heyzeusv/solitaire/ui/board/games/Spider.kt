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
import com.heyzeusv.solitaire.util.notInOrder

data object Spider : Games.SpiderFamily() {
    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_spider
    override val familyId: Int = R.string.games_family_spider
    override val previewId: Int = R.drawable.preview_spider
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_spider,
        stockRulesId = R.string.spider_stock_rules,
        wasteRulesId = null,
        foundationRulesId = R.string.spider_foundation_rules,
        tableauRulesId = R.string.spider_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_SPIDER

    /**
     *  [GameRules]
     */
    override val baseDeck: List<Card> = List(104) { Card(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.Ten
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.TwoDecks
    override val autocompleteAvailable: Boolean = false
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Eight
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Ten

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean = false

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEachIndexed { index, tableau ->
            if (index < 4) {
                val cards = List(6) { stock.remove() }
                tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
            } else {
                val cards = List(5) { stock.remove() }
                tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
            }
        }
    }

    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        foundationList.forEach { it.reset() }
    }

    override fun canAddToTableauNonEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit == tLast.suit && cFirst.value == tLast.value - 1
    }

    override fun canAddToTableauEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return !cardsToAdd.notInOrder()
    }

    override fun gameWon(foundation: List<Foundation>): Boolean {
        // each foundation should have Ace to King which is 13 cards
        foundation.forEach { if (it.truePile.size != 13) return false }
        return true
    }
}