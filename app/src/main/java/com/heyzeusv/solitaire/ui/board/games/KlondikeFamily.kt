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
import com.heyzeusv.solitaire.util.Suits

/**
 *  Games that belong to Klondike family.
 */
sealed class KlondikeFamily : Games() {
    override val familyId: Int = R.string.games_family_klondike

    override val resetFaceUpAmount: ResetFaceUpAmount = ResetFaceUpAmount.One
    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Unlimited

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.faceDownExists()) return false }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEachIndexed { index, tableau ->
            val cards = List(index + 1) { stock.remove() }
            tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    override fun canAddToTableauRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit.color != tLast.suit.color && cFirst.value == tLast.value - 1
    }
}

class KlondikeTurnOne : KlondikeFamily() {
    override val nameId: Int = R.string.games_klondike_turn_one
    override val previewId: Int = R.drawable.preview_klondike_turn_one
    override val dataStoreEnum: Game = Game.GAME_KLONDIKETURNONE
}

class KlondikeTurnThree : KlondikeFamily() {
    override val nameId: Int = R.string.games_klondike_turn_three
    override val previewId: Int = R.drawable.preview_klondike_turn_three
    override val dataStoreEnum: Game = Game.GAME_KLONDIKETURNTHREE

    override val drawAmount: DrawAmount = DrawAmount.Three
}

class ClassicWestcliff : KlondikeFamily() {
    override val nameId: Int = R.string.games_classic_westcliff
    override val previewId: Int = R.drawable.preview_classic_westcliff
    override val dataStoreEnum: Game = Game.GAME_CLASSIC_WESTCLIFF

    override val baseDeck: List<Card> = List(48) { Card((it % 12) + 1, getSuit(it)) }
    override val redeals: Redeals = Redeals.None
    override val maxScore: MaxScore = MaxScore.ONE_DECK_NO_ACES
    override val anyCardCanStartPile: Boolean = true

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(3) { stock.remove() }
            tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    override fun resetFoundation(foundationList: List<Foundation>) {
        foundationList.forEach { it.reset(listOf(Card(0, it.suit, true))) }
    }

    override fun getSuit(i: Int) = when (i / 12) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }
}

class Easthaven : KlondikeFamily() {
    override val nameId: Int = R.string.games_easthaven
    override val previewId: Int = R.drawable.preview_easthaven
    override val dataStoreEnum: Game = Game.GAME_EASTHAVEN

    override val drawAmount: DrawAmount = DrawAmount.Seven
    override val redeals: Redeals = Redeals.None
    override val anyCardCanStartPile: Boolean = true

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.faceDownExists() || it.notInOrderOrAltColor()) return false }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(3) { stock.remove() }
            tableau.reset(resetFlipCard(cards, resetFaceUpAmount))
        }
    }

    override fun canAddToTableauRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        val tLast = tableau.truePile.last()
        val cFirst = cardsToAdd.first()

        return cFirst.suit.color != tLast.suit.color &&
               cFirst.value == tLast.value - 1 &&
               tableau.notInOrderOrAltColor(cardsToAdd)
    }
}