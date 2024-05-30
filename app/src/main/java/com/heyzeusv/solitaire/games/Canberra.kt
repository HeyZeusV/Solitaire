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

data object Canberra : Games.YukonFamily() {
    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit == tLast.suit && cFirst.value == tLast.value - 1
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        val cFirst = cardsToAdd.first()
        return cFirst.value == 12
    }

    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_canberra
    override val familyId: Int = R.string.games_family_yukon
    override val previewId: Int = R.drawable.preview_australian_patience
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_australian_patience,
        stockRulesId = R.string.canberra_stock_rules,
        wasteRulesId = R.string.canberra_waste_rules,
        foundationRulesId = R.string.canberra_foundation_rules,
        tableauRulesId = R.string.canberra_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_CANBERRA
    override val dbName: String = "canberra"

    /**
     *  [GameRules]
     */
    override val baseDeck: List<CardLogic> = List(52) { CardLogic(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.Four
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Once
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.OneDeck
    override val autocompleteAvailable: Boolean = true
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Four
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Seven

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        for (i in 0 until numOfTableauPiles.amount) {
            if (
                tableauList[i].isMultiSuit() ||
                tableauList[i].notInOrder()
            ) return false
        }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = List(4) { stock.remove() }
            tableauList[i].reset(resetFlipCard(cards, AustralianPatience.resetFaceUpAmount))
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