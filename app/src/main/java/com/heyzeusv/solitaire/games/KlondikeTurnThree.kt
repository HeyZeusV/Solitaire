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

data object KlondikeTurnThree : Games.KlondikeFamily() {
    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit.color != tLast.suit.color && cFirst.value == tLast.value - 1
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean {
        val cFirst = cardsToAdd.first()
        return cFirst.value == 12
    }
    
    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_klondike_turn_three
    override val familyId: Int = R.string.games_family_klondike
    override val previewId: Int = R.drawable.preview_klondike_turn_three
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_klondike_turn_three,
        stockRulesId = R.string.klondike_turn_three_stock_rules,
        wasteRulesId = R.string.klondike_turn_three_waste_rules,
        foundationRulesId = R.string.klondike_turn_three_foundation_rules,
        tableauRulesId = R.string.klondike_turn_three_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_KLONDIKETURNTHREE
    override val dbName: String = "klondikeTurnThree"

    /**
     *  [GameRules]
     */
    override val baseDeck: List<CardLogic> = List(52) { CardLogic(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.Three
    override val redeals: Redeals = Redeals.Unlimited
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.OneDeck
    override val autocompleteAvailable: Boolean = true
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Four
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Seven

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        for (i in 0 until numOfTableauPiles.amount) {
            if (tableauList[i].faceDownExists()) return false
        }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = List(i + 1) { stock.remove() }
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