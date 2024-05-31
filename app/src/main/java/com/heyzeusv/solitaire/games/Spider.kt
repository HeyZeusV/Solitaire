package com.heyzeusv.solitaire.games

import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.NumberOfPiles
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.ResetFaceUpAmount
import com.heyzeusv.solitaire.util.StartingScore
import com.heyzeusv.solitaire.util.inOrder
import com.heyzeusv.solitaire.util.isNotMultiSuit

data object Spider : Games.SpiderFamily() {
    override fun canAddToFoundation(foundation: Foundation, cardToAdd: Card): Boolean {
        return false
    }

    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.value == tLast.value - 1 &&
               cardsToAdd.inOrder() && cardsToAdd.isNotMultiSuit()
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return cardsToAdd.inOrder() && cardsToAdd.isNotMultiSuit()
    }

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
    override val dbName: String = "spider"

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
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = if (i < 4) {
                List(6) { stock.remove() }
            } else {
                List(5) { stock.remove() }
            }
            tableauList[i].reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) {
        for (i in 0 until numOfFoundationPiles.amount) {
            foundationList[i].reset()
        }
    }

    override fun gameWon(foundation: List<Foundation>): Boolean {
        // each foundation should have Ace to King which is 13 cards
        for (i in 0 until numOfFoundationPiles.amount) {
            if (foundation[i].truePile.size != 13) return false
        }
        return true
    }
}