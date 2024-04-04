package com.heyzeusv.solitaire.ui.board.games

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.ui.toolbar.menu.GamesMenu
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.Suits

/**
 *  Subclasses of this will not be games exactly, but instead the families the game belongs to in
 *  order to have an extra layer of organization.
 */
sealed class Games : GameInfo, GameRules {
    override val baseDeck: List<Card> = List(52) { Card(it % 13, getSuit(it))}
    override val maxScore: MaxScore = MaxScore.ONE_DECK

    private val familySubclasses = Games::class.sealedSubclasses
    val gameSubclasses = familySubclasses.flatMap { it.sealedSubclasses }

}

/**
 *  Info that each game requires. Each game has its own unique [nameId] and belongs to a family
 *  which is retrieved using [familyId]. Each game on [GamesMenu] has a small preview image,
 *  [previewId], to give users an idea of the type of game it is.
 */
interface GameInfo {
    @get:StringRes val nameId: Int
    @get:StringRes val familyId: Int
    @get:DrawableRes val previewId: Int
}

/**
 *  Rules that are shared between each game, but with different values. [baseDeck] is a list of all
 *  Cards that are available when resetting a game. [drawAmount] is the amount of Cards drawn per
 *  click of [Stock]. [redeals] is the number of times [Stock] can be refilled from [Waste].
 *  [maxScore] refers to the max score users can get from just placing Cards in [Foundation].
 */
interface GameRules {
    val baseDeck: List<Card>
    val drawAmount: DrawAmount
    val redeals: Redeals
    val maxScore: MaxScore

    /**
     *  Each game has its own rules to determine if user has progressed far enough to activate
     *  autocomplete. [Tableau] contains the various rules, but each [Games] subclass must call
     *  the appropriate rule(s) on given [tableauList].
     */
    fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean

    /**
     *  Each game has its own way of setting up [Tableau] piles when a user resets the game.
     *  Using [Stock.remove] from given [stock], cards are added to each [Tableau] in [tableauList]
     *  using [Tableau.reset].
     */
    fun resetTableau(tableauList: List<Tableau>, stock: Stock)

    /**
     *  Some games requires additional steps to fully reset their [Foundation] piles. Using
     *  [Foundation.reset], cards will be added to given each [Foundation] in [foundationList].
     */
    fun resetFoundation(foundationList: List<Foundation>) { }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     */
     fun getSuit(i: Int) = when (i / 13) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
     }
}