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

data object AcesUp : Games.AcesUpVariants() {
    /**
     *  Checks if given [cardToAdd] is one more or less than last card of [foundation] truePile.
     */
    override fun canAddToFoundation(foundation: Foundation, cardToAdd: Card): Boolean {
        return true
    }

    /**
     *  Checks if the last card belonging to Tableau represented by [tableauIndex] can be added to
     *  Foundation by checking the remaining top cards in [tableauList].
     */
    override fun canAddToFoundation(tableauList: List<Tableau>, tableauIndex: Int): Boolean {
        if (tableauList[tableauIndex].truePile.isEmpty()) return false
        val cardToAdd = tableauList[tableauIndex].truePile.last()
        tableauList.forEachIndexed { index, tableau ->
            if (index != tableauIndex) {
                val tableauLast = tableau.truePile.last()
                if (tableauLast.suit == cardToAdd.suit &&
                    (tableauLast.value == 0 || tableauLast.value > cardToAdd.value)
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return false
    }

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        return cardsToAdd.size == 1
    }

    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_aces_up
    override val familyId: Int = R.string.games_family_other
    override val previewId: Int = R.drawable.preview_aces_up
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_aces_up,
        stockRulesId = R.string.aces_up_stock_rules,
        wasteRulesId = null,
        foundationRulesId = R.string.aces_up_foundation_rules,
        tableauRulesId = R.string.aces_up_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_ACES_UP

    /**
     *  [GameRules]
     */
    override val baseDeck: List<Card> = List(52) { Card(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.Four
    override val redeals: Redeals = Redeals.None
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.OneDeckNoAces
    override val autocompleteAvailable: Boolean = false
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Four
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Four

    /**
     *  Autocomplete is not available for this game.
     */
    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean = false

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        for (i in 0 until numOfTableauPiles.amount) {
            val cards = List(1) { stock.remove() }
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
        return foundation[3].truePile.size == 48
    }
}