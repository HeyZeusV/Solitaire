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

data object FortyThieves : Games.FortyThievesFamily() {
    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        if (cardsToAdd.size > 1) return false
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit == tLast.suit && cFirst.value == tLast.value - 1
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        return cardsToAdd.size == 1
    }

    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_forty_thieves
    override val familyId: Int = R.string.games_family_forty_thieves
    override val previewId: Int = R.drawable.preview_forty_thieves
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_forty_thieves,
        stockRulesId = R.string.forty_thieves_stock_rules,
        wasteRulesId = R.string.forty_thieves_waste_rules,
        foundationRulesId = R.string.forty_thieves_foundation_rules,
        tableauRulesId = R.string.forty_thieves_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_FORTY_THIEVES
    override val dbName: String = "fortyThieves"

    /**
     *  [GameRules]
     */
    override val baseDeck: List<CardLogic> = List(104) { CardLogic(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.Four
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.TwoDecks
    override val autocompleteAvailable: Boolean = true
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Eight
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Ten

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        for (i in 0 until numOfTableauPiles.amount) {
            if (tableauList[i].isMultiSuit() && tableauList[i].notInOrder()) return false
        }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = List(4) { stock.remove() }
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