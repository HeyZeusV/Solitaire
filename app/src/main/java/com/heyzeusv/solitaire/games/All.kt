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

data object All : Games.Other() {
    /**
     *  [BaseGame]
     */
    override fun canAddToNonEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean = false

    override fun canAddToEmptyTableau(tableau: Tableau, cardsToAdd: List<CardLogic>): Boolean = false

    /**
     *  [GameInfo]
     */
    override val nameId: Int = R.string.games_all
    override val familyId: Int = R.string.games_family_klondike
    override val previewId: Int = R.drawable.preview_klondike_turn_one
    override val gamePileRules: GamePileRules = GamePileRules(
        rulesId = R.drawable.rules_klondike_turn_one,
        stockRulesId = R.string.klondike_turn_one_stock_rules,
        wasteRulesId = R.string.klondike_turn_one_waste_rules,
        foundationRulesId = R.string.klondike_turn_one_foundation_rules,
        tableauRulesId = R.string.klondike_turn_one_tableau_rules
    )
    override val dataStoreEnum: Game = Game.GAME_ALL
    override val dbName: String = "all"

    /**
     *  [GameRules]
     */
    override val baseDeck: List<CardLogic> = List(52) { CardLogic(it % 13, getSuit(it)) }
    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Unlimited
    override val startingScore: StartingScore = StartingScore.Zero
    override val maxScore: MaxScore = MaxScore.OneDeck
    override val autocompleteAvailable: Boolean = true
    override val numOfFoundationPiles: NumberOfPiles = NumberOfPiles.Four
    override val numOfTableauPiles: NumberOfPiles = NumberOfPiles.Seven

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean = true

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) { }

    override fun resetFoundation(foundationList: List<Foundation>, stock: Stock) { }

    override fun gameWon(foundation: List<Foundation>): Boolean = true
}