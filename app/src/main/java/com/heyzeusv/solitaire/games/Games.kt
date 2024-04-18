package com.heyzeusv.solitaire.games

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.piles.Waste
import com.heyzeusv.solitaire.menu.GamesMenu
import com.heyzeusv.solitaire.menu.RulesMenu
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.NumberOfPiles
import com.heyzeusv.solitaire.util.Redeals
import com.heyzeusv.solitaire.util.ResetFaceUpAmount
import com.heyzeusv.solitaire.util.StartingScore
import com.heyzeusv.solitaire.util.Suits

/**
 *  Subclasses of this will not be games exactly, but instead the families the game belongs to in
 *  order to have an extra layer of organization.
 */
sealed class Games : GameInfo, GameRules {
    sealed class KlondikeFamily : Games()
    sealed class YukonFamily : Games()
    sealed class GolfFamily : Games()
    sealed class SpiderFamily: Games()

    /**
     *  Checks if it is possible for [cardsToAdd] to be added to given [tableau] using
     *  [canAddToTableauNonEmptyRule] and [canAddToTableauNonEmptyRule].
     */
    fun canAddToTableau(tableau: Tableau, cardsToAdd: List<Card>): Boolean {
        if (cardsToAdd.isEmpty()) return false

        val cFirst = cardsToAdd.first()
        tableau.truePile.let {
            // can't add card to its own pile in single deck games
            if (baseDeck.size == 54 && it.contains(cFirst)) return false
            if (it.isNotEmpty()) {
                if (canAddToTableauNonEmptyRule(tableau, cardsToAdd)) return true
            } else if (canAddToTableauEmptyRule(tableau, cardsToAdd)) {
                return true
            }
            return false
        }
    }

    /**
     *  Checks if given [cardsToAdd] matches this [Foundation.suit] and its value matches the size
     *  of [foundation] truePile.
     */
    open fun canAddToFoundation(foundation: Foundation, cardsToAdd: List<Card>): Boolean {
        if (cardsToAdd.isEmpty()) return false
        val firstCard = cardsToAdd[0]
        return firstCard.suit == foundation.suit && firstCard.value == foundation.truePile.size
    }

    /**
     *  Using [resetFaceUpAmount], flips given [ResetFaceUpAmount.amount] of Cards face up in
     *  [cards] and returns it as a new list.
     */
    protected fun resetFlipCard(
        cards: List<Card>,
        resetFaceUpAmount: ResetFaceUpAmount
    ): List<Card> {
        val mCards = cards.toMutableList()
        for (i in mCards.size.downTo(mCards.size - resetFaceUpAmount.amount + 1)) {
            try {
                mCards[i - 1] = mCards[i - 1].copy(faceUp = true)
            } catch (e: IndexOutOfBoundsException) {
                break
            }
        }
        return mCards
    }

    companion object {
//        private val familySubclasses = Games::class.sealedSubclasses
//        val gameSubclasses = familySubclasses.flatMap { it.sealedSubclasses }

        val orderedSubclasses: List<Games> = listOf(
            KlondikeTurnOne, KlondikeTurnThree, ClassicWestcliff,
            Easthaven, Yukon, Alaska,
            Russian, AustralianPatience, Canberra,
            Golf, PuttPutt, GolfRush,
            Spider, SpiderTwoSuits, SpiderOneSuit
        )

        /**
         *  Returns [Games] subclass associated with given [game] from Proto DataSture [Game].
         */
        fun getGameClass(game: Game): Games {
            return when (game) {
                Game.GAME_KLONDIKETURNONE -> KlondikeTurnOne
                Game.GAME_KLONDIKETURNTHREE -> KlondikeTurnThree
                Game.GAME_AUSTRALIAN_PATIENCE -> AustralianPatience
                Game.GAME_CANBERRA -> Canberra
                Game.GAME_YUKON -> Yukon
                Game.GAME_ALASKA -> Alaska
                Game.GAME_RUSSIAN -> Russian
                Game.GAME_CLASSIC_WESTCLIFF -> ClassicWestcliff
                Game.GAME_EASTHAVEN -> Easthaven
                Game.GAME_GOLF -> Golf
                Game.GAME_PUTT_PUTT -> PuttPutt
                Game.GAME_GOLF_RUSH -> GolfRush
                Game.GAME_SPIDER -> Spider
                Game.GAME_SPIDER_TWO_SUITS -> SpiderTwoSuits
                Game.GAME_SPIDER_ONE_SUIT -> SpiderOneSuit
                Game.UNRECOGNIZED -> KlondikeTurnOne
            }
        }
    }
}

/**
 *  Info that each game requires. Each game has its own unique [nameId] and belongs to a family
 *  which is retrieved using [familyId]. Each game on [GamesMenu] has a small preview image,
 *  [previewId], to give users an idea of the type of game it is. [dataStoreEnum] is used to ensure
 *  the correct [GameStats] is updated when updating stats. [gamePileRules] contains resource ids
 *  for data to be displayed in [RulesMenu] which are essentially typed out versions of [GameRules]
 *  for users to understand how the game plays.
 */
interface GameInfo {
    @get:StringRes val nameId: Int
    @get:StringRes val familyId: Int
    @get:DrawableRes val previewId: Int
    val gamePileRules: GamePileRules
    val dataStoreEnum: Game
}

/**
 *  Rules that are shared between each game, but with different values. [baseDeck] is a list of all
 *  Cards that are available when resetting a game. [resetFaceUpAmount] is the number of Cards that
 *  start face up on game reset. [drawAmount] is the amount of Cards drawn per click of [Stock].
 *  [redeals] is the number of times [Stock] can be refilled from [Waste]. [startingScore] refers
 *  to the score a game starts with due to starting with cards in [Foundation]. [maxScore] refers
 *  to the max score users can get from just placing Cards in [Foundation]. [autocompleteAvailable]
 *  determines if game offers autocomplete if [autocompleteTableauCheck] passes.
 *  [numOfFoundationPiles] refers to the number of Foundation piles the game uses.
 *  [numOfTableauPiles] refers to the number of Tableau piles the game uses.
 */
interface GameRules {
    val baseDeck: List<Card>
    val resetFaceUpAmount: ResetFaceUpAmount
    val drawAmount: DrawAmount
    val redeals: Redeals
    val startingScore: StartingScore
    val maxScore: MaxScore
    val autocompleteAvailable: Boolean
    val numOfFoundationPiles: NumberOfPiles
    val numOfTableauPiles: NumberOfPiles

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
     *  [stock] is used if a random card is needed at start.
     */
    fun resetFoundation(foundationList: List<Foundation>, stock: Stock)

    /**
     *  Each game has its own rules when it comes to adding [cardsToAdd] to given [tableau] pile
     *  when truePile is not empty.
     */
    fun canAddToTableauNonEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean

    /**
     *  Each game has its own rules when it comes to adding [cardsToAdd] to given [tableau] pile
     *  when truePile is empty.
     */
    fun canAddToTableauEmptyRule(tableau: Tableau, cardsToAdd: List<Card>): Boolean

    /**
     *  Checks if game has been won depending on [foundation] truePile condition.
     */
    fun gameWon(foundation: List<Foundation>): Boolean

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     *  Cards 52-64 -> Clubs
     *  Cards 65-77 -> Diamonds
     *  Cards 78-90 -> Hearts
     *  Cards 91-103 -> Spades
     */
     fun getSuit(i: Int) = when (i / 13) {
        0, 4 -> Suits.CLUBS
        1, 5 -> Suits.DIAMONDS
        2, 6 -> Suits.HEARTS
        else -> Suits.SPADES
     }
}

/**
 *  Contains resource ids for drawables and strings used in [RulesMenu].
 */
data class GamePileRules(
    @DrawableRes val rulesId: Int,
    @StringRes private val stockRulesId: Int?,
    @StringRes private val wasteRulesId: Int?,
    @StringRes private val foundationRulesId: Int?,
    @StringRes private val tableauRulesId: Int?
) {
    val pileRulesIds = listOf(stockRulesId, wasteRulesId, foundationRulesId, tableauRulesId)
}